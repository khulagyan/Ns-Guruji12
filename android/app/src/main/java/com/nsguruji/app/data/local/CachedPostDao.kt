package com.nsguruji.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CachedPostDao {

    @Query("SELECT * FROM cached_posts ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getRecentPosts(limit: Int = 20): List<CachedPostEntity>

    @Query("SELECT * FROM cached_posts WHERE id = :id LIMIT 1")
    suspend fun getPostById(id: Long): CachedPostEntity?

    @Query("SELECT * FROM cached_posts WHERE categoryId = :categoryId ORDER BY cachedAt DESC")
    suspend fun getPostsByCategory(categoryId: Long): List<CachedPostEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<CachedPostEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: CachedPostEntity)

    @Query("DELETE FROM cached_posts WHERE cachedAt < :threshold")
    suspend fun clearOldCache(threshold: Long)
}
