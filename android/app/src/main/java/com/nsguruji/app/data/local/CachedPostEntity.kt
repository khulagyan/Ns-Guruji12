package com.nsguruji.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_posts")
data class CachedPostEntity(
    @PrimaryKey
    val id: Long,
    val title: String,
    val excerpt: String,
    val content: String,
    val link: String,
    val date: String,
    val modified: String?,
    val featuredImageUrl: String?,
    val categoryName: String?,
    val categoryId: Long? = null,
    val cachedAt: Long = System.currentTimeMillis()
)
