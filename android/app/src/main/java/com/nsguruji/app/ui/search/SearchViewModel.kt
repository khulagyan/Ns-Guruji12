package com.nsguruji.app.ui.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nsguruji.app.data.api.NetworkResult
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.data.repository.BookmarkRepository
import com.nsguruji.app.data.repository.WordPressRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val searchResults: List<Post> = emptyList(),
    val recentSearches: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val errorMessage: String? = null,
    val bookmarkedIds: Set<Long> = emptySet()
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val wordPressRepository = WordPressRepository(application)
    private val bookmarkRepository = BookmarkRepository(application)
    private val prefs = application.getSharedPreferences("nsguruji_search_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadRecentSearches()
        observeBookmarks()
    }

    private fun observeBookmarks() {
        viewModelScope.launch {
            bookmarkRepository.getAllBookmarks().collectLatest { bookmarks ->
                _uiState.update { it.copy(bookmarkedIds = bookmarks.map { b -> b.id }.toSet()) }
            }
        }
    }

    private fun loadRecentSearches() {
        val savedSet = prefs.getStringSet("recent_queries", emptySet()) ?: emptySet()
        _uiState.update { it.copy(recentSearches = savedSet.toList().take(10)) }
    }

    fun onQueryChange(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        searchJob?.cancel()

        if (newQuery.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), hasSearched = false, isLoading = false) }
            return
        }

        // Debounce search by 500ms
        searchJob = viewModelScope.launch {
            delay(500)
            executeSearch(newQuery)
        }
    }

    fun executeSearch(searchQuery: String) {
        val trimmed = searchQuery.trim()
        if (trimmed.isBlank()) return

        saveRecentSearch(trimmed)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, hasSearched = true) }
            val result = wordPressRepository.getPosts(
                page = 1,
                perPage = 20,
                searchQuery = trimmed
            )

            when (result) {
                is NetworkResult.Success -> {
                    _uiState.update {
                        it.copy(
                            searchResults = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            searchResults = emptyList(),
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> {}
            }
        }
    }

    private fun saveRecentSearch(query: String) {
        val current = _uiState.value.recentSearches.toMutableList()
        current.remove(query)
        current.add(0, query)
        val updated = current.take(10)
        prefs.edit().putStringSet("recent_queries", updated.toSet()).apply()
        _uiState.update { it.copy(recentSearches = updated) }
    }

    fun clearRecentSearches() {
        prefs.edit().remove("recent_queries").apply()
        _uiState.update { it.copy(recentSearches = emptyList()) }
    }

    fun toggleBookmark(post: Post) {
        viewModelScope.launch {
            val isBookmarked = _uiState.value.bookmarkedIds.contains(post.id)
            bookmarkRepository.toggleBookmark(post, isBookmarked)
        }
    }
}
