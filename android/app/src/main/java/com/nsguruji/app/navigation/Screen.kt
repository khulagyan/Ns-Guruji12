package com.nsguruji.app.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Categories : Screen("categories")
    object CategoryArticles : Screen("category_articles/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Long, categoryName: String): String {
            return "category_articles/$categoryId/${Uri.encode(categoryName)}"
        }
    }
    object Search : Screen("search")
    object Saved : Screen("saved")
    object Article : Screen("article/{postId}") {
        fun createRoute(postId: Long): String {
            return "article/$postId"
        }
    }
    object About : Screen("about")
    object PrivacyPolicy : Screen("privacy_policy")
}
