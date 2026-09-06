package com.nsguruji.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsguruji.app.R
import com.nsguruji.app.ui.theme.PrimaryDarkRed
import com.nsguruji.app.ui.theme.SurfaceLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String = "NS Guruji",
    isRootScreen: Boolean = true,
    onMenuClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    actions: @Composable () -> Unit = {
        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = SurfaceLight
            )
        }
        IconButton(onClick = onNotificationClick) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = SurfaceLight
            )
        }
    }
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isRootScreen) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_nsguruji_logo),
                        contentDescription = "NS Guruji Logo",
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = SurfaceLight
                    ),
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            if (isRootScreen) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Drawer",
                        tint = SurfaceLight
                    )
                }
            } else {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = SurfaceLight
                    )
                }
            }
        },
        actions = {
            actions()
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = PrimaryDarkRed
        )
    )
}
