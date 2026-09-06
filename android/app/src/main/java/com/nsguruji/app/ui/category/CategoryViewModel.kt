package com.nsguruji.app.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class CategoryArticlesUiState(
    val posts: List<Post> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true,
    val bookmarkedIds: Set<Long> = emptySet()
)

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val wordPressRepository = WordPressRepository(application)
    private val bookmarkRepository = BookmarkRepository(application)

    private val _categoryState = MutableStateFlow(CategoryUiState())
    val categoryState: StateFlow<CategoryUiState> = _categoryState.asStateFlow()

    private val _articlesState = MutableStateFlow(CategoryArticlesUiState())
    val articlesState: StateFlow<CategoryArticlesUiState> = _articlesState.asStateFlow()

    init {
        loadCategories()
        observeBookmarks()
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarks().collectLatest { list ->
                _articlesState.update { it.copy(bookmarkedIds = list.map { b -> b.id }.toSet()) }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            _categoryState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = wordPressRepository.getCategories()) {
                is NetworkResult.Success -> {
                    // Filter out empty categories
                    val validCategories = result.data.filter { it.count > 0 }
                    _categoryState.update {
                        it.copy(
                            categories = validCategories,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _categoryState.update {
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

    fun loadCategoryArticles(categoryId: Long, page: Int = 1) {
        viewModelScope.launch {
            if (page == 1) {
                _articlesState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _articlesState.update { it.copy(isLoadingMore = true) }
            }

            val result = wordPressRepository.getPosts(
                page = page,
                perPage = 10,
                categoryId = categoryId
            )

            when (result) {
                is NetworkResult.Success -> {
                    val newPosts = if (page == 1) result.data else _articlesState.value.posts + result.data
                    val hasMore = page < result.totalPages && result.data.isNotEmpty()
                    _articlesState.update {
                        it.copy(
                            posts = newPosts,
                            isLoading = false,
                            isLoadingMore = false,
                            currentPage = page,
                            hasMorePages = hasMore,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _articlesState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = if (page == 1) result.message else null
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    fun toggleBookmark(post: Post) {
        viewModelScope.launch {
            val isBookmarked = _articlesState.value.bookmarkedIds.contains(post.id)
            bookmarkRepository.toggleBookmark(post, isBookmarked)
        }
    }
}
