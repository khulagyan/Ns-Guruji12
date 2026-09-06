package com.nsguruji.app.data.model

import com.google.gson.annotations.SerializedName

data class Category(
    val id: Long,
    val count: Int,
    val description: String?,
    val link: String?,
    val name: String,
    val slug: String,
    val taxonomy: String?,
    val parent: Long?
) {
    fun getCleanName(): String {
        return name
            .replace("&amp;", "&")
            .replace("&#8211;", "-")
            .replace("&quot;", "\"")
            .trim()
    }
}
