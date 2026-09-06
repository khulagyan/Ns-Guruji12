package com.nsguruji.app.ui.article

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nsguruji.app.R
import com.nsguruji.app.ads.AdMobManager
import com.nsguruji.app.ads.BannerAdView
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.components.ArticleCard
import com.nsguruji.app.ui.components.EmptyErrorState
import com.nsguruji.app.ui.components.HtmlContentText
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.BorderLight
import com.nsguruji.app.ui.theme.PrimaryBlue
import com.nsguruji.app.ui.theme.PrimaryDarkRed
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceCard
import com.nsguruji.app.ui.theme.SurfaceLight
import com.nsguruji.app.ui.theme.TextMuted
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary
import com.nsguruji.app.utils.DateFormatter
import com.nsguruji.app.utils.ShareUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(
    postId: Long,
    onBackClick: () -> Unit,
    onRelatedArticleClick: (Post) -> Unit,
    viewModel: ArticleViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(postId) {
        viewModel.loadArticle(postId)
    }

    val post = uiState.post

    Scaffold(
        topBar = {
            AppTopBar(
                title = "NS Guruji",
                isRootScreen = false,
                onBackClick = {
                    // Check interstitial ad before navigating back
                    (context as? Activity)?.let { activity ->
                        AdMobManager.showInterstitialWithFrequencyCap(activity) {
                            onBackClick()
                        }
                    } ?: onBackClick()
                },
                actions = {
                    if (post != null) {
                        IconButton(onClick = { viewModel.toggleBookmark() }) {
                            Icon(
                                imageVector = if (uiState.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = SurfaceLight
                            )
                        }
                        IconButton(onClick = {
                            ShareUtils.shareArticle(
                                context = context,
                                title = post.getCleanTitle(),
                                url = post.link
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = SurfaceLight
                            )
                        }
                    }
                }
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
                uiState.errorMessage != null && post == null -> {
                    EmptyErrorState(
                        title = "लेख लोड नहीं हो सका",
                        message = uiState.errorMessage ?: "त्रुटि",
                        retryAction = { viewModel.loadArticle(postId) }
                    )
                }
                post != null -> {
                    val categoryName = post.getCategoryNames().firstOrNull() ?: "महत्वपूर्ण"
                    val formattedPublished = DateFormatter.formatPostDate(post.date)
                    val formattedModified = DateFormatter.formatPostDate(post.modified)
                    val imageUrl = post.getFeaturedImageUrl()

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Category Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(PrimaryRed)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = categoryName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = SurfaceLight,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Article Title
                        Text(
                            text = post.getCleanTitle(),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 21.sp,
                                lineHeight = 28.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Date & Metadata info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = formattedPublished,
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                                )
                            }

                            if (formattedModified.isNotBlank() && formattedModified != formattedPublished) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.EditCalendar,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "अपडेट: $formattedModified",
                                        style = MaterialTheme.typography.bodySmall.copy(color = PrimaryBlue)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Featured Image
                        if (!imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageUrl)
                                    .crossfade(true)
                                    .placeholder(R.drawable.ic_nsguruji_logo)
                                    .build(),
                                contentDescription = post.getCleanTitle(),
                                contentScale = ContentScale.FillWidth,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Banner Ad right before the content
                        BannerAdView(modifier = Modifier.padding(vertical = 4.dp))

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rendered WordPress HTML content with Hindi fonts, tables, lists
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.padding(14.dp)) {
                                HtmlContentText(
                                    htmlContent = post.content.rendered
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        HorizontalDivider(color = BorderLight)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Related Articles
                        if (uiState.relatedPosts.isNotEmpty()) {
                            Text(
                                text = "संबंधित लेख (Related Articles)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDarkRed,
                                    fontSize = 18.sp
                                ),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            uiState.relatedPosts.forEach { relatedPost ->
                                ArticleCard(
                                    post = relatedPost,
                                    isBookmarked = false,
                                    onArticleClick = { onRelatedArticleClick(relatedPost) },
                                    onBookmarkToggle = {}
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
