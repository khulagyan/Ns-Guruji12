package com.nsguruji.app.ui.saved

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nsguruji.app.ads.BannerAdView
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.components.ArticleCard
import com.nsguruji.app.ui.components.EmptyErrorState
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.PrimaryRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onArticleClick: (Post) -> Unit,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: SavedViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "सेव किए गए लेख (Saved)",
                isRootScreen = true,
                onMenuClick = onMenuClick,
                onSearchClick = onSearchClick
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
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryRed)
                    }
                }
                uiState.bookmarkedArticles.isEmpty() -> {
                    EmptyErrorState(
                        title = "कोई सेव किया गया लेख नहीं है",
                        message = "अपनी पसंद के किसी भी लेख पर बुकमार्क आइकन दबाकर उसे यहाँ ऑफलाइन पढ़ने के लिए सेव करें।",
                        icon = Icons.Outlined.BookmarkBorder
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.bookmarkedArticles, key = { it.id }) { post ->
                            ArticleCard(
                                post = post,
                                isBookmarked = true,
                                onArticleClick = onArticleClick,
                                onBookmarkToggle = { viewModel.removeBookmark(post.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
