package com.nsguruji.app.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nsguruji.app.ads.BannerAdView
import com.nsguruji.app.data.model.Post
import com.nsguruji.app.ui.components.AppTopBar
import com.nsguruji.app.ui.components.ArticleCard
import com.nsguruji.app.ui.components.EmptyErrorState
import com.nsguruji.app.ui.theme.BackgroundLight
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceLight
import com.nsguruji.app.ui.theme.TextMuted
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    onArticleClick: (Post) -> Unit,
    onMenuClick: () -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            AppTopBar(
                title = "खोजें (Search)",
                isRootScreen = true,
                onMenuClick = onMenuClick,
                actions = {}
            )
        },
        bottomBar = {
            BannerAdView()
        },
        containerColor = BackgroundLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input Field
            Box(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    placeholder = {
                        Text(
                            text = "नौकरी, प्रवेश पत्र या योजना खोजें...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = PrimaryRed
                        )
                    },
                    trailingIcon = {
                        if (uiState.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear text",
                                    tint = TextMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = SurfaceLight,
                        focusedBorderColor = PrimaryRed,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            focusManager.clearFocus()
                            viewModel.executeSearch(uiState.query)
                        }
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Body Area
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryRed)
                    }
                }
                uiState.hasSearched && uiState.searchResults.isEmpty() -> {
                    EmptyErrorState(
                        title = "कोई परिणाम नहीं मिला",
                        message = "‘${uiState.query}’ के लिए कोई लेख नहीं मिला। कृपया कोई अन्य शब्द खोजें।",
                        icon = Icons.Outlined.SearchOff
                    )
                }
                uiState.searchResults.isNotEmpty() -> {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = "खोज परिणाम (${uiState.searchResults.size})",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                ),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }
                        items(uiState.searchResults, key = { it.id }) { post ->
                            val isBookmarked = uiState.bookmarkedIds.contains(post.id)
                            ArticleCard(
                                post = post,
                                isBookmarked = isBookmarked,
                                onArticleClick = onArticleClick,
                                onBookmarkToggle = { viewModel.toggleBookmark(post) }
                            )
                        }
                    }
                }
                else -> {
                    // Display recent searches and popular suggestions
                    if (uiState.recentSearches.isNotEmpty()) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "हाल में खोजे गए",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                }
                                TextButton(onClick = { viewModel.clearRecentSearches() }) {
                                    Text("सभी हटाएं", color = PrimaryRed, fontSize = 12.sp)
                                }
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                uiState.recentSearches.forEach { query ->
                                    SuggestionChip(
                                        onClick = {
                                            viewModel.onQueryChange(query)
                                            viewModel.executeSearch(query)
                                        },
                                        label = { Text(query) }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Popular topic tags
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "लोकप्रिय खोजें (Popular Searches)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val popularTags = listOf("RRB NTPC", "SSC GD", "Admit Card", "Sarkari Yojana", "Police Bharti", "Kisan Yojana", "Anganwadi")
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            popularTags.forEach { tag ->
                                SuggestionChip(
                                    onClick = {
                                        viewModel.onQueryChange(tag)
                                        viewModel.executeSearch(tag)
                                    },
                                    label = { Text(tag) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
