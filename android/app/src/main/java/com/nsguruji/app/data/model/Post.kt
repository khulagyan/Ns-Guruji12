package com.nsguruji.app.data.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: Long,
    val date: String,
    @SerializedName("date_gmt")
    val dateGmt: String?,
    val modified: String?,
    @SerializedName("modified_gmt")
    val modifiedGmt: String?,
    val slug: String,
    val status: String,
    val link: String,
    val title: RenderedText,
    val content: RenderedText,
    val excerpt: RenderedText,
    val author: Long?,
    @SerializedName("featured_media")
    val featuredMedia: Long?,
    val categories: List<Long> = emptyList(),
    @SerializedName("_embedded")
    val embedded: EmbeddedData? = null
) {
    /**
     * Extracts the featured image URL from the embedded data, falling back to null.
     */
    fun getFeaturedImageUrl(): String? {
        val wpFeaturedMedia = embedded?.wpFeaturedMedia?.firstOrNull()
        return wpFeaturedMedia?.sourceUrl 
            ?: wpFeaturedMedia?.mediaDetails?.sizes?.large?.sourceUrl
            ?: wpFeaturedMedia?.mediaDetails?.sizes?.medium?.sourceUrl
    }

    /**
     * Extracts category names from embedded terms.
     */
    fun getCategoryNames(): List<String> {
        val terms = embedded?.wpTerms?.flatten() ?: emptyList()
        return terms.filter { it.taxonomy == "category" }.map { it.name }
    }

    /**
     * Extracts author name from embedded author data.
     */
    fun getAuthorName(): String {
        return embedded?.author?.firstOrNull()?.name ?: "NS Guruji Team"
    }

    /**
     * Clean excerpt without HTML tags.
     */
    fun getPlainExcerpt(): String {
        return excerpt.rendered
            .replace(Regex("<[^>]*>"), "")
            .replace("&nbsp;", " ")
            .replace("&#8230;", "...")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .trim()
    }

    /**
     * Clean title decoding common HTML entities.
     */
    fun getCleanTitle(): String {
        return title.rendered
            .replace("&nbsp;", " ")
            .replace("&#8230;", "...")
            .replace("&amp;", "&")
            .replace("&#8217;", "'")
            .replace("&#8216;", "'")
            .replace("&quot;", "\"")
            .replace("&#038;", "&")
            .trim()
    }
}

data class RenderedText(
    val rendered: String,
    val protected: Boolean? = false
)

data class EmbeddedData(
    val author: List<EmbeddedAuthor>? = null,
    @SerializedName("wp:featuredmedia")
    val wpFeaturedMedia: List<EmbeddedMedia>? = null,
    @SerializedName("wp:term")
    val wpTerms: List<List<EmbeddedTerm>>? = null
)

data class EmbeddedAuthor(
    val id: Long,
    val name: String,
    val url: String?,
    val description: String?
)

data class EmbeddedMedia(
    val id: Long,
    @SerializedName("source_url")
    val sourceUrl: String?,
    @SerializedName("media_details")
    val mediaDetails: MediaDetails?
)

data class MediaDetails(
    val width: Int?,
    val height: Int?,
    val sizes: MediaSizes?
)

data class MediaSizes(
    val medium: MediaSizeItem?,
    val large: MediaSizeItem?,
    val thumbnail: MediaSizeItem?,
    val full: MediaSizeItem?
)

data class MediaSizeItem(
    val file: String?,
    val width: Int?,
    val height: Int?,
    @SerializedName("source_url")
    val sourceUrl: String?
)

data class EmbeddedTerm(
    val id: Long,
    val link: String?,
    val name: String,
    val slug: String,
    val taxonomy: String
)
