package com.nsguruji.app.data.repository

import android.content.Context
import com.nsguruji.app.data.local.AppDatabase
import com.nsguruji.app.data.local.BookmarkEntity
import com.nsguruji.app.data.model.Post
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class BookmarkRepository(context: Context) {

    private val bookmarkDao = AppDatabase.getDatabase(context).bookmarkDao()

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = bookmarkDao.getAllBookmarks()

    fun isBookmarked(postId: Long): Flow<Boolean> = bookmarkDao.isBookmarked(postId)

    suspend fun saveBookmark(post: Post) = withContext(Dispatchers.IO) {
        val entity = BookmarkEntity(
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
        bookmarkDao.insertBookmark(entity)
    }

    suspend fun removeBookmark(postId: Long) = withContext(Dispatchers.IO) {
        bookmarkDao.deleteBookmarkById(postId)
    }

    suspend fun toggleBookmark(post: Post, isCurrentlyBookmarked: Boolean) = withContext(Dispatchers.IO) {
        if (isCurrentlyBookmarked) {
            bookmarkDao.deleteBookmarkById(post.id)
        } else {
            saveBookmark(post)
        }
    }
}
