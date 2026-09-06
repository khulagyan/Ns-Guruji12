package com.nsguruji.app.data.repository

import android.content.Context
import com.nsguruji.app.data.api.NetworkResult
import com.nsguruji.app.data.api.RetrofitClient
import com.nsguruji.app.data.api.WordPressApiService
import com.nsguruji.app.data.local.AppDatabase
import com.nsguruji.app.data.local.CachedPostEntity
import com.nsguruji.app.data.model.Category
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.data.model.RenderedText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WordPressRepository(private val context: Context) {

    private val apiService: WordPressApiService = RetrofitClient.getService(context)
    private val database: AppDatabase = AppDatabase.getDatabase(context)
    private val cachedPostDao = database.cachedPostDao()

    /**
     * Fetches posts from WordPress REST API with offline caching fallback.
     */
    suspend fun getPosts(
        page: Int = 1,
        perPage: Int = 10,
        categoryId: Long? = null,
        searchQuery: String? = null
    ): NetworkResult<List<Post>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPosts(
                page = page,
                perPage = perPage,
                categoryId = categoryId,
                searchQuery = searchQuery,
                embed = true
            )

            if (response.isSuccessful) {
                val posts = response.body() ?: emptyList()
                val totalCount = response.headers()["X-WP-Total"]?.toIntOrNull() ?: posts.size
                val totalPages = response.headers()["X-WP-TotalPages"]?.toIntOrNull() ?: 1

                // Cache newly fetched posts to local DB for offline access
                if (page == 1 && searchQuery.isNullOrBlank()) {
                    val cacheEntities = posts.map { post ->
                        CachedPostEntity(
                            id = post.id,
                            title = post.getCleanTitle(),
                            excerpt = post.getPlainExcerpt(),
                            content = post.content.rendered,
                            link = post.link,
                            date = post.date,
                            modified = post.modified,
                            featuredImageUrl = post.getFeaturedImageUrl(),
                            categoryName = post.getCategoryNames().firstOrNull(),
                            categoryId = categoryId
                        )
                    }
                    cachedPostDao.insertPosts(cacheEntities)
                }

                NetworkResult.Success(posts, totalCount = totalCount, totalPages = totalPages)
            } else {
                // If API returned error, attempt to load cached posts for page 1
                if (page == 1) {
                    val cached = getCachedPosts(categoryId)
                    if (cached.isNotEmpty()) {
                        return@withContext NetworkResult.Success(cached, totalCount = cached.size, totalPages = 1)
                    }
                }
                NetworkResult.Error("सर्वर प्रतिक्रिया त्रुटि: ${response.code()}")
            }
        } catch (e: Exception) {
            // Network failure: Attempt to serve from local Room cache
            if (page == 1) {
                val cached = getCachedPosts(categoryId)
                if (cached.isNotEmpty()) {
                    return@withContext NetworkResult.Success(cached, totalCount = cached.size, totalPages = 1)
                }
            }
            NetworkResult.Error(
                "इंटरनेट कनेक्शन नहीं है या सर्वर अनुपलब्ध है। कृपया अपना नेटवर्क जांचें।",
                e
            )
        }
    }

    /**
     * Fetches post by ID, checking API first, then local cache.
     */
    suspend fun getPostById(id: Long): NetworkResult<Post> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPostById(id, embed = true)
            if (response.isSuccessful && response.body() != null) {
                val post = response.body()!!
                // Update cache
                cachedPostDao.insertPost(
                    CachedPostEntity(
                        id = post.id,
                        title = post.getCleanTitle(),
                        excerpt = post.getPlainExcerpt(),
                        content = post.content.rendered,
                        link = post.link,
                        date = post.date,
                        modified = post.modified,
                        featuredImageUrl = post.getFeaturedImageUrl(),
                        categoryName = post.getCategoryNames().firstOrNull()
                    )
                )
                NetworkResult.Success(post)
            } else {
                val cached = cachedPostDao.getPostById(id)
                if (cached != null) {
                    NetworkResult.Success(cachedToPost(cached))
                } else {
                    NetworkResult.Error("लेख नहीं मिला।")
                }
            }
        } catch (e: Exception) {
            val cached = cachedPostDao.getPostById(id)
            if (cached != null) {
                NetworkResult.Success(cachedToPost(cached))
            } else {
                NetworkResult.Error("इंटरनेट त्रुटि: लेख लोड करने में असमर्थ।", e)
            }
        }
    }

    /**
     * Resolves an article from a slug or WordPress URL path (Deep linking).
     */
    suspend fun getPostBySlug(slug: String): NetworkResult<Post> = withContext(Dispatchers.IO) {
        try {
            val cleanSlug = slug.trim().trim('/')
            val response = apiService.getPostBySlug(cleanSlug, embed = true)
            if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                NetworkResult.Success(response.body()!!.first())
            } else {
                NetworkResult.Error("यह लेख उपलब्ध नहीं है।")
            }
        } catch (e: Exception) {
            NetworkResult.Error("लेख खोलने में विफल: ${e.localizedMessage}", e)
        }
    }

    /**
     * Dynamically fetches WordPress categories.
     */
    suspend fun getCategories(): NetworkResult<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCategories(page = 1, perPage = 50)
            if (response.isSuccessful) {
                val categories = response.body() ?: emptyList()
                NetworkResult.Success(categories)
            } else {
                NetworkResult.Error("श्रेणियां लोड करने में त्रुटि")
            }
        } catch (e: Exception) {
            NetworkResult.Error("नेटवर्क समस्या: श्रेणियां लोड नहीं हो सकीं", e)
        }
    }

    private suspend fun getCachedPosts(categoryId: Long?): List<Post> {
        val cachedList = if (categoryId != null) {
            cachedPostDao.getPostsByCategory(categoryId)
        } else {
            cachedPostDao.getRecentPosts(20)
        }
        return cachedList.map { cachedToPost(it) }
    }

    private fun cachedToPost(entity: CachedPostEntity): Post {
        return Post(
            id = entity.id,
            date = entity.date,
            dateGmt = null,
            modified = entity.modified,
            modifiedGmt = null,
            slug = "",
            status = "publish",
            link = entity.link,
            title = RenderedText(entity.title),
            content = RenderedText(entity.content),
            excerpt = RenderedText(entity.excerpt),
            author = 1,
            featuredMedia = 0,
            categories = if (entity.categoryId != null) listOf(entity.categoryId) else emptyList(),
            embedded = null
        )
    }
}
