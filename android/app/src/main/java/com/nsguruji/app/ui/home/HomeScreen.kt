package com.nsguruji.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nsguruji.app.ads.BannerAdView
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.components.ArticleCard
import com.nsguruji.app.ui.components.CategoryFilterRow
import com.nsguruji.app.ui.components.EmptyErrorState
import com.nsguruji.app.ui.components.ShimmerCard
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onArticleClick: (Post) -> Unit,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Detect when user scrolled to the bottom for infinite pagination
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && uiState.hasMorePages && !uiState.isLoadingMore) {
            viewModel.loadMore()
        }
    }

    // Pull to refresh state
    val pullToRefreshState = rememberPullToRefreshState()
    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.refresh()
        }
    }

    LaunchedEffect(uiState.isRefreshing) {
        if (!uiState.isRefreshing) {
            pullToRefreshState.endRefresh()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "NS Guruji",
                isRootScreen = true,
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick,
                onNotificationClick = onNotificationClick
            )
        },
        bottomBar = {
            // Banner ad at the bottom of the home screen
            BannerAdView()
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Horizontal category filter chips
                CategoryFilterRow(
                    categories = uiState.categories,
                    selectedCategoryId = uiState.selectedCategoryId,
                    onSelectCategory = { categoryId ->
                        viewModel.selectCategory(categoryId)
                    }
                )

                // Main Content List
                when {
                    uiState.isLoading && uiState.posts.isEmpty() -> {
                        // Skeleton shimmer loading
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(5) {
                                ShimmerCard()
                            }
                        }
                    }
                    uiState.errorMessage != null && uiState.posts.isEmpty() -> {
                        EmptyErrorState(
                            title = "सर्वर से कनेक्ट नहीं हो सका",
                            message = uiState.errorMessage ?: "कृपया इंटरनेट कनेक्शन जांचें।",
                            retryAction = { viewModel.refresh() }
                        )
                    }
                    uiState.posts.isEmpty() -> {
                        EmptyErrorState(
                            title = "कोई लेख उपलब्ध नहीं है",
                            message = "इस श्रेणी में फिलहाल कोई नया लेख नहीं है।",
                            retryAction = { viewModel.refresh() }
                        )
                    }
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FiberNew,
                                        contentDescription = null,
                                        tint = PrimaryRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ताजा लेख (Latest Articles)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 17.sp,
                                            color = TextPrimary
                                        )
                                    )
                                }
                            }

                            itemsIndexed(
                                items = uiState.posts,
                                key = { _, post -> post.id }
                            ) { index, post ->
                                val isBookmarked = uiState.bookmarkedIds.contains(post.id)
                                ArticleCard(
                                    post = post,
                                    isBookmarked = isBookmarked,
                                    onArticleClick = onArticleClick,
                                    onBookmarkToggle = { viewModel.toggleBookmark(post) }
                                )

                                // Insert an ad banner after every 5 articles
                                if ((index + 1) % 5 == 0 && index < uiState.posts.size - 1) {
                                    BannerAdView(
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    )
                                }
                            }

                            if (uiState.isLoadingMore) {
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

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                contentColor = PrimaryRed
            )
        }
    }
}
