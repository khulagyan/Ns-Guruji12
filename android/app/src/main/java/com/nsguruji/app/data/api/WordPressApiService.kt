package com.nsguruji.app.data.api

import com.nsguruji.app.data.model.Category
import com.nsguruji.app.data.model.Post
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WordPressApiService {

    @GET("wp-json/wp/v2/posts")
    suspend fun getPosts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("categories") categoryId: Long? = null,
        @Query("search") searchQuery: String? = null,
        @Query("_embed") embed: Boolean = true
    ): Response<List<Post>>

    @GET("wp-json/wp/v2/posts/{id}")
    suspend fun getPostById(
        @Path("id") id: Long,
        @Query("_embed") embed: Boolean = true
    ): Response<Post>

    @GET("wp-json/wp/v2/posts")
    suspend fun getPostBySlug(
        @Query("slug") slug: String,
        @Query("_embed") embed: Boolean = true
    ): Response<List<Post>>

    @GET("wp-json/wp/v2/categories")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50,
        @Query("hide_empty") hideEmpty: Boolean = true,
        @Query("orderby") orderby: String = "count",
        @Query("order") order: String = "desc"
    ): Response<List<Category>>
}
