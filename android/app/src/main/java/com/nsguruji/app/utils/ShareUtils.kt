package com.nsguruji.app.utils

import android.content.Context
import android.content.Intent
import com.nsguruji.app.config.AppConfig

object ShareUtils {

    fun shareArticle(context: Context, title: String, url: String) {
        val shareBody = """
            Read this article on NS Guruji:
            $title

            $url
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        context.startActivity(Intent.createChooser(intent, "शेयर करें"))
    }

    fun shareApp(context: Context) {
        val shareBody = """
            NS Guruji - सरकारी नौकरी, योजना, प्रवेश पत्र और रिजल्ट्स की सबसे तेज़ और सटीक जानकारी अपने मोबाइल पर पाएं!
            
            डाउनलोड करें या वेबसाइट देखें:
            ${AppConfig.WEBSITE_URL}
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "NS Guruji App")
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        context.startActivity(Intent.createChooser(intent, "ऐप शेयर करें"))
    }
}
