package com.nsguruji.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.nsguruji.app.navigation.Screen
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceLight
import com.nsguruji.app.ui.theme.TextMuted
import com.nsguruji.app.ui.theme.TextPrimary

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem(
            route = Screen.Home.route,
            title = "होम",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavItem(
            route = Screen.Categories.route,
            title = "श्रेणियां",
            selectedIcon = Icons.Filled.Category,
            unselectedIcon = Icons.Outlined.Category
        ),
        BottomNavItem(
            route = Screen.Search.route,
            title = "खोजें",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search
        ),
        BottomNavItem(
            route = Screen.Saved.route,
            title = "सेव किए गए",
            selectedIcon = Icons.Filled.Bookmark,
            unselectedIcon = Icons.Outlined.BookmarkBorder
        )
    )

    NavigationBar(
        containerColor = SurfaceLight,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(text = item.title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryRed,
                    selectedTextColor = PrimaryRed,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = SurfaceLight
                )
            )
        }
    }
}
