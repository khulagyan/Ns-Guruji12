package com.nsguruji.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nsguruji.app.R
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.ui.theme.PrimaryBlue
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceCard
import com.nsguruji.app.ui.theme.SurfaceLight
import com.nsguruji.app.ui.theme.TextMuted
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary
import com.nsguruji.app.utils.DateFormatter

@Composable
fun ArticleCard(
    post: Post,
    isBookmarked: Boolean,
    onArticleClick: (Post) -> Unit,
    onBookmarkToggle: (Post) -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryName = post.getCategoryNames().firstOrNull() ?: "अपडेट"
    val imageUrl = post.getFeaturedImageUrl()
    val formattedDate = DateFormatter.formatPostDate(post.date)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onArticleClick(post) },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Featured Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .placeholder(R.drawable.ic_nsguruji_logo)
                            .error(R.drawable.ic_nsguruji_logo)
                            .build(),
                        contentDescription = post.getCleanTitle(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(PrimaryBlue.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NS Guruji",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Category Badge Overlay
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryRed)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = categoryName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = SurfaceLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Article Details
            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                // Title
                Text(
                    text = post.getCleanTitle(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Short Excerpt
                val excerpt = post.getPlainExcerpt()
                if (excerpt.isNotBlank()) {
                    Text(
                        text = excerpt,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Metadata Row (Date + Bookmark Action)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { onBookmarkToggle(post) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "Saved" else "Save",
                            tint = if (isBookmarked) PrimaryRed else TextMuted
                        )
                    }
                }
            }
        }
    }
}
