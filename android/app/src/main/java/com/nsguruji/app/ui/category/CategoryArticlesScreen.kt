package com.nsguruji.app.ui.category

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nsguruji.app.ads.BannerAdView
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.components.ArticleCard
import com.nsguruji.app.ui.components.EmptyErrorState
import com.nsguruji.app.ui.components.ShimmerCard
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryArticlesScreen(
    categoryId: Long,
    categoryName: String,
    onBackClick: () -> Unit,
    onArticleClick: (Post) -> Unit,
    viewModel: CategoryViewModel = viewModel()
) {
    val state by viewModel.articlesState.collectAsState()

    LaunchedEffect(categoryId) {
        viewModel.loadCategoryArticles(categoryId)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = categoryName,
                isRootScreen = false,
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            BannerAdView()
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.posts.isEmpty() -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(5) {
                            ShimmerCard()
                        }
                    }
                }
                state.errorMessage != null && state.posts.isEmpty() -> {
                    EmptyErrorState(
                        title = "लेख लोड नहीं हो सके",
                        message = state.errorMessage ?: "त्रुटि",
                        retryAction = { viewModel.loadCategoryArticles(categoryId) }
                    )
                }
                state.posts.isEmpty() -> {
                    EmptyErrorState(
                        title = "कोई लेख नहीं मिला",
                        message = "इस श्रेणी में अभी कोई लेख नहीं है।"
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.posts, key = { it.id }) { post ->
                            val isBookmarked = state.bookmarkedIds.contains(post.id)
                            ArticleCard(
                                post = post,
                                isBookmarked = isBookmarked,
                                onArticleClick = onArticleClick,
                                onBookmarkToggle = { viewModel.toggleBookmark(post) }
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = PrimaryRed,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
