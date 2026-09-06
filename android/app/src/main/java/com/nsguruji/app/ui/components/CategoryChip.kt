package com.nsguruji.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsguruji.app.data.model.Category
import com.nsguruji.app.ui.theme.BorderLight
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceLight
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary

data class CategoryFilter(
    val id: Long?,
    val name: String
)

@Composable
fun CategoryFilterRow(
    categories: List<Category>,
    selectedCategoryId: Long?,
    onSelectCategory: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Default categories matching NS Guruji editorial hierarchy
    val defaultList = mutableListOf(
        CategoryFilter(null, "सभी (Latest)"),
        CategoryFilter(133L, "Govt Jobs"),
        CategoryFilter(194L, "Admit Card"),
        CategoryFilter(192L, "Results"),
        CategoryFilter(193L, "Govt Schemes"),
        CategoryFilter(112L, "Financial News"),
        CategoryFilter(284L, "Latest Updates")
    )

    // Merge dynamically fetched categories if they have different IDs
    val existingIds = defaultList.mapNotNull { it.id }.toSet()
    val additional = categories.filter { it.id !in existingIds && it.count > 0 }
        .map { CategoryFilter(it.id, it.getCleanName()) }
    
    val fullList = defaultList + additional

    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(fullList) { filter ->
            val isSelected = selectedCategoryId == filter.id
            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) PrimaryRed else SurfaceLight)
                    .clickable { onSelectCategory(filter.id) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = filter.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isSelected) SurfaceLight else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp
                    )
                )
            }
        }
    }
}
