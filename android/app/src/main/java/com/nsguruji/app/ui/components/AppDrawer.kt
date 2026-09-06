package com.nsguruji.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nsguruji.app.R
import com.nsguruji.app.navigation.Screen
import com.nsguruji.app.ui.theme.BorderLight
import com.nsguruji.app.ui.theme.PrimaryDarkRed
import com.nsguruji.app.ui.theme.PrimaryRed
import com.nsguruji.app.ui.theme.SurfaceLight
import com.nsguruji.app.ui.theme.TextPrimary
import com.nsguruji.app.ui.theme.TextSecondary

@Composable
fun AppDrawer(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onShareApp: () -> Unit,
    onRateUs: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = SurfaceLight
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Brand Logo & Description
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryDarkRed)
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_nsguruji_logo),
                            contentDescription = "NS Guruji Logo",
                            modifier = Modifier
                                .size(54.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "NS Guruji",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SurfaceLight,
                                    fontSize = 22.sp
                                )
                            )
                            Text(
                                text = "nsguruji.com",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SurfaceLight.copy(alpha = 0.85f),
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "सरकारी नौकरी, योजना, प्रवेश पत्र और रिजल्ट्स का सबसे विश्वसनीय पोर्टल।",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = SurfaceLight.copy(alpha = 0.9f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Sections
            NavigationDrawerItem(
                label = { Text("होम (Home)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                selected = currentRoute == Screen.Home.route,
                onClick = {
                    onNavigate(Screen.Home.route)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = PrimaryRed.copy(alpha = 0.1f),
                    selectedTextColor = PrimaryRed,
                    selectedIconColor = PrimaryRed
                )
            )

            NavigationDrawerItem(
                label = { Text("श्रेणियां (Categories)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Category, contentDescription = null) },
                selected = currentRoute == Screen.Categories.route,
                onClick = {
                    onNavigate(Screen.Categories.route)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = PrimaryRed.copy(alpha = 0.1f),
                    selectedTextColor = PrimaryRed,
                    selectedIconColor = PrimaryRed
                )
            )

            NavigationDrawerItem(
                label = { Text("सेव किए गए (Saved)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                selected = currentRoute == Screen.Saved.route,
                onClick = {
                    onNavigate(Screen.Saved.route)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = PrimaryRed.copy(alpha = 0.1f),
                    selectedTextColor = PrimaryRed,
                    selectedIconColor = PrimaryRed
                )
            )

            NavigationDrawerItem(
                label = { Text("खोजें (Search)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Search, contentDescription = null) },
                selected = currentRoute == Screen.Search.route,
                onClick = {
                    onNavigate(Screen.Search.route)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = PrimaryRed.copy(alpha = 0.1f),
                    selectedTextColor = PrimaryRed,
                    selectedIconColor = PrimaryRed
                )
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = BorderLight)

            NavigationDrawerItem(
                label = { Text("ऐप शेयर करें (Share App)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Share, contentDescription = null) },
                selected = false,
                onClick = {
                    onShareApp()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            NavigationDrawerItem(
                label = { Text("रेट करें (Rate Us)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Star, contentDescription = null) },
                selected = false,
                onClick = {
                    onRateUs()
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), color = BorderLight)

            NavigationDrawerItem(
                label = { Text("गोपनीयता नीति (Privacy)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Policy, contentDescription = null) },
                selected = currentRoute == Screen.PrivacyPolicy.route,
                onClick = {
                    onNavigate(Screen.PrivacyPolicy.route)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            NavigationDrawerItem(
                label = { Text("हमारे बारे में (About Us)", fontWeight = FontWeight.Medium) },
                icon = { Icon(Icons.Default.Info, contentDescription = null) },
                selected = currentRoute == Screen.About.route,
                onClick = {
                    onNavigate(Screen.About.route)
                    onCloseDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )

            Spacer(modifier = Modifier.weight(1f, fill = false))
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Version 1.0.0 • NS Guruji",
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )
        }
    }
}
