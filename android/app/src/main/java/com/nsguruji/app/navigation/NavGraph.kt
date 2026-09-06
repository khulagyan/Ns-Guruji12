package com.nsguruji.app.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nsguruji.app.ads.AdMobManager
import com.nsguruji.app.config.AppConfig
import com.nsguruji.app.ui.about.AboutScreen
import com.nsguruji.app.ui.article.ArticleScreen
import com.nsguruji.app.ui.category.CategoryArticlesScreen
import com.nsguruji.app.ui.category.CategoryScreen
import com.nsguruji.app.ui.components.AppDrawer
import com.nsguruji.app.ui.components.BottomNavBar
import com.nsguruji.app.ui.home.HomeScreen
import com.nsguruji.app.ui.privacy.PrivacyPolicyScreen
import com.nsguruji.app.ui.saved.SavedScreen
import com.nsguruji.app.ui.search.SearchScreen
import com.nsguruji.app.utils.ShareUtils
import kotlinx.coroutines.launch

@Composable
fun NSGurujiNavGraph(
    navController: NavHostController = rememberNavController(),
    initialPostId: Long? = null
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show bottom bar on primary tabs
    val isBottomBarVisible = currentRoute in listOf(
        Screen.Home.route,
        Screen.Categories.route,
        Screen.Search.route,
        Screen.Saved.route
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isBottomBarVisible,
        drawerContent = {
            AppDrawer(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onShareApp = { ShareUtils.shareApp(context) },
                onRateUs = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppConfig.WEBSITE_URL))
                        context.startActivity(intent)
                    }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            bottomBar = {
                if (isBottomBarVisible) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        onNavigate = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (initialPostId != null && initialPostId > 0) {
                    Screen.Article.createRoute(initialPostId)
                } else {
                    Screen.Home.route
                },
                modifier = Modifier.padding(innerPadding)
            ) {
                // Home Screen
                composable(Screen.Home.route) {
                    HomeScreen(
                        onArticleClick = { post ->
                            // Check frequency capped interstitial before navigation
                            (context as? Activity)?.let { activity ->
                                AdMobManager.showInterstitialWithFrequencyCap(activity) {
                                    navController.navigate(Screen.Article.createRoute(post.id))
                                }
                            } ?: navController.navigate(Screen.Article.createRoute(post.id))
                        },
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSearchClick = { navController.navigate(Screen.Search.route) },
                        onNotificationClick = {
                            // Quick refresh or navigate
                            navController.navigate(Screen.Home.route)
                        }
                    )
                }

                // Categories Screen
                composable(Screen.Categories.route) {
                    CategoryScreen(
                        onCategoryClick = { category ->
                            navController.navigate(
                                Screen.CategoryArticles.createRoute(category.id, category.getCleanName())
                            )
                        },
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSearchClick = { navController.navigate(Screen.Search.route) }
                    )
                }

                // Category Articles Screen
                composable(
                    route = Screen.CategoryArticles.route,
                    arguments = listOf(
                        navArgument("categoryId") { type = NavType.LongType },
                        navArgument("categoryName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: 0L
                    val categoryName = Uri.decode(backStackEntry.arguments?.getString("categoryName") ?: "श्रेणी")

                    CategoryArticlesScreen(
                        categoryId = categoryId,
                        categoryName = categoryName,
                        onBackClick = { navController.popBackStack() },
                        onArticleClick = { post ->
                            navController.navigate(Screen.Article.createRoute(post.id))
                        }
                    )
                }

                // Search Screen
                composable(Screen.Search.route) {
                    SearchScreen(
                        onArticleClick = { post ->
                            navController.navigate(Screen.Article.createRoute(post.id))
                        },
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                }

                // Saved Articles Screen
                composable(Screen.Saved.route) {
                    SavedScreen(
                        onArticleClick = { post ->
                            navController.navigate(Screen.Article.createRoute(post.id))
                        },
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSearchClick = { navController.navigate(Screen.Search.route) }
                    )
                }

                // Article Screen
                composable(
                    route = Screen.Article.route,
                    arguments = listOf(navArgument("postId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getLong("postId") ?: 0L
                    ArticleScreen(
                        postId = postId,
                        onBackClick = { navController.popBackStack() },
                        onRelatedArticleClick = { relatedPost ->
                            navController.navigate(Screen.Article.createRoute(relatedPost.id))
                        }
                    )
                }

                // About Screen
                composable(Screen.About.route) {
                    AboutScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }

                // Privacy Policy Screen
                composable(Screen.PrivacyPolicy.route) {
                    PrivacyPolicyScreen(
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
