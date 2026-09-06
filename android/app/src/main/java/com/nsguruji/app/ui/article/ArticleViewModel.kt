package com.nsguruji.app.ui.article

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nsguruji.app.data.api.NetworkResult
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.data.repository.BookmarkRepository
import com.nsguruji.app.data.repository.WordPressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ArticleUiState(
    val post: Post? = null,
    val isLoading: Boolean = true,
    val isBookmarked: Boolean = false,
    val errorMessage: String? = null,
    val relatedPosts: List<Post> = emptyList()
)

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    private val wordPressRepository = WordPressRepository(application)
    private val bookmarkRepository = BookmarkRepository(application)

    private val _uiState = MutableStateFlow(ArticleUiState())
    val uiState: StateFlow<ArticleUiState> = _uiState.asStateFlow()

    fun loadArticle(postId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Observe bookmark state for this post
            launch {
                bookmarkRepository.isBookmarked(postId).collectLatest { isSaved ->
                    _uiState.update { it.copy(isBookmarked = isSaved) }
                }
            }

            when (val result = wordPressRepository.getPostById(postId)) {
                is NetworkResult.Success -> {
                    val post = result.data
                    _uiState.update {
                        it.copy(
                            post = post,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                    loadRelatedPosts(post)
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun loadRelatedPosts(post: Post) {
        val categoryId = post.categories.firstOrNull() ?: return
        viewModelScope.launch {
            val result = wordPressRepository.getPosts(
                page = 1,
                perPage = 4,
                categoryId = categoryId
            )
            if (result is NetworkResult.Success) {
                val filtered = result.data.filter { it.id != post.id }.take(3)
                _uiState.update { it.copy(relatedPosts = filtered) }
            }
        }
    }

    fun toggleBookmark() {
        val currentPost = _uiState.value.post ?: return
        viewModelScope.launch {
            bookmarkRepository.toggleBookmark(currentPost, _uiState.value.isBookmarked)
        }
    }
}
