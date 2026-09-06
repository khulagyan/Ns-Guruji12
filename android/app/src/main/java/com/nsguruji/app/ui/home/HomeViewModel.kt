package com.nsguruji.app.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nsguruji.app.config.AppConfig
import com.nsguruji.app.data.api.NetworkResult
import com.nsguruji.app.data.model.Category
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.data.repository.BookmarkRepository
import com.nsguruji.app.data.repository.WordPressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val posts: List<Post> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val bookmarkedIds: Set<Long> = emptySet()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val wordPressRepository = WordPressRepository(application)
    private val bookmarkRepository = BookmarkRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeBookmarks()
        loadCategories()
        loadPosts(page = 1, isRefresh = false)
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarks().collectLatest { bookmarks ->
                val ids = bookmarks.map { it.id }.toSet()
                _uiState.update { it.copy(bookmarkedIds = ids) }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            when (val result = wordPressRepository.getCategories()) {
                is NetworkResult.Success -> {
                    _uiState.update { it.copy(categories = result.data) }
                }
                else -> {
                    // Fail silently for categories; default list remains
                }
            }
        }
    }

    fun loadPosts(page: Int = 1, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
            } else if (page == 1) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true) }
            }

            val result = wordPressRepository.getPosts(
                page = page,
                perPage = AppConfig.DEFAULT_PAGE_SIZE,
                categoryId = _uiState.value.selectedCategoryId
            )

            when (result) {
                is NetworkResult.Success -> {
                    val newPosts = if (page == 1) result.data else _uiState.value.posts + result.data
                    val hasMore = page < result.totalPages && result.data.isNotEmpty()
                    _uiState.update {
                        it.copy(
                            posts = newPosts,
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            currentPage = page,
                            hasMorePages = hasMore,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            isLoadingMore = false,
                            errorMessage = if (page == 1 && it.posts.isEmpty()) result.message else null
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun refresh() {
        loadPosts(page = 1, isRefresh = true)
    }

    fun loadMore() {
        if (!_uiState.value.isLoadingMore && _uiState.value.hasMorePages && !_uiState.value.isLoading) {
            loadPosts(page = _uiState.value.currentPage + 1, isRefresh = false)
        }
    }

    fun selectCategory(categoryId: Long?) {
        if (_uiState.value.selectedCategoryId != categoryId) {
            _uiState.update {
                it.copy(
                    selectedCategoryId = categoryId,
                    currentPage = 1,
                    posts = emptyList(),
                    isLoading = true,
                    hasMorePages = true
                )
            }
            loadPosts(page = 1, isRefresh = false)
        }
    }

    fun toggleBookmark(post: Post) {
        viewModelScope.launch {
            val isCurrentlyBookmarked = _uiState.value.bookmarkedIds.contains(post.id)
            bookmarkRepository.toggleBookmark(post, isCurrentlyBookmarked)
        }
    }
}
