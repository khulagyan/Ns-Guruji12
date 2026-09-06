package com.nsguruji.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
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
    val savedAt: Long = System.currentTimeMillis()
)
