package com.nsguruji.app.ui.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nsguruji.app.ads.BannerAdView
import com.nsguruji.app.data.model.Category
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.components.EmptyErrorState
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.PrimaryBlue
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceCard
import com.nsguruji.app.ui.theme.TextMuted
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    onCategoryClick: (Category) -> Unit,
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit,
    viewModel: CategoryViewModel = viewModel()
) {
    val state by viewModel.categoryState.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                title = "श्रेणियां (Categories)",
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
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryRed)
                    }
                }
                state.errorMessage != null -> {
                    EmptyErrorState(
                        title = "श्रेणियां लोड नहीं हो सकीं",
                        message = state.errorMessage ?: "त्रुटि",
                        retryAction = { viewModel.loadCategories() }
                    )
                }
                state.categories.isEmpty() -> {
                    EmptyErrorState(
                        title = "कोई श्रेणी उपलब्ध नहीं है",
                        message = "कृपया कुछ देर बाद प्रयास करें।"
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.categories, key = { it.id }) { category ->
                            CategoryItemCard(
                                category = category,
                                onClick = { onCategoryClick(category) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItemCard(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = category.getCleanName(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                    )
                    Text(
                        text = "${category.count} लेख उपलब्ध",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
