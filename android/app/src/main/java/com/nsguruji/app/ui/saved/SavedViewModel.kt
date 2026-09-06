package com.nsguruji.app.ui.saved

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nsguruji.app.data.local.BookmarkEntity
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.data.model.RenderedText
import com.nsguruji.app.data.repository.BookmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SavedUiState(
    val bookmarkedArticles: List<Post> = emptyList(),
    val isLoading: Boolean = true
)

class SavedViewModel(application: Application) : AndroidViewModel(application) {

    private val bookmarkRepository = BookmarkRepository(application)

    private val _uiState = MutableStateFlow(SavedUiState())
    val uiState: StateFlow<SavedUiState> = _uiState.asStateFlow()

    init {
        loadBookmarks()
    }

    private fun loadBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarks().collectLatest { entities ->
                val posts = entities.map { entityToPost(it) }
                _uiState.update {
                    it.copy(
                        bookmarkedArticles = posts,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun removeBookmark(postId: Long) {
        viewModelScope.launch {
            bookmarkRepository.removeBookmark(postId)
        }
    }

    private fun entityToPost(entity: BookmarkEntity): Post {
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
            author = null,
            featuredMedia = null,
            categories = emptyList(),
            embedded = null
        )
    }
}
