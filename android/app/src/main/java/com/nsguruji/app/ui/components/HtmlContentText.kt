package com.nsguruji.app.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun HtmlContentText(
    htmlContent: String,
    modifier: Modifier = Modifier,
    onLinkClicked: ((String) -> Unit)? = null
) {
    val styledHtml = """
        <!DOCTYPE html>
        <html lang="hi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <link rel="preconnect" href="https://fonts.googleapis.com">
            <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
            <link href="https://fonts.googleapis.com/css2?family=Mukta:wght@400;500;600;700&display=swap" rel="stylesheet">
            <style>
                * {
                    box-sizing: border-box;
                    margin: 0;
                    padding: 0;
                }
                body {
                    font-family: 'Mukta', system-ui, -apple-system, sans-serif;
                    font-size: 16px;
                    line-height: 1.7;
                    color: #1e293b;
                    background-color: transparent;
                    padding: 4px 2px;
                    word-wrap: break-word;
                }
                h1, h2, h3, h4, h5, h6 {
                    color: #0f172a;
                    margin-top: 1.2em;
                    margin-bottom: 0.5em;
                    font-weight: 700;
                    line-height: 1.35;
                }
                h1 { font-size: 1.5rem; }
                h2 { font-size: 1.35rem; color: #b71c1c; border-bottom: 2px solid #fee2e2; padding-bottom: 4px; }
                h3 { font-size: 1.2rem; color: #0d47a1; }
                h4 { font-size: 1.1rem; }
                p {
                    margin-bottom: 1em;
                }
                strong, b {
                    font-weight: 700;
                    color: #0f172a;
                }
                ul, ol {
                    margin-left: 20px;
                    margin-bottom: 1.2em;
                }
                li {
                    margin-bottom: 0.5em;
                }
                a {
                    color: #d32f2f;
                    text-decoration: underline;
                    font-weight: 600;
                }
                img {
                    max-width: 100% !important;
                    height: auto !important;
                    border-radius: 8px;
                    margin: 12px 0;
                    display: block;
                }
                table {
                    width: 100% !important;
                    border-collapse: collapse;
                    margin: 16px 0;
                    font-size: 14px;
                    background: #ffffff;
                    border: 1px solid #e2e8f0;
                    border-radius: 8px;
                    overflow: hidden;
                }
                th, td {
                    border: 1px solid #e2e8f0;
                    padding: 10px 12px;
                    text-align: left;
                }
                th {
                    background-color: #f1f5f9;
                    font-weight: 700;
                    color: #0f172a;
                }
                tr:nth-child(even) {
                    background-color: #f8fafc;
                }
                blockquote {
                    border-left: 4px solid #d32f2f;
                    padding: 8px 14px;
                    margin: 14px 0;
                    background-color: #fff5f5;
                    color: #475569;
                    font-style: italic;
                    border-radius: 0 8px 8px 0;
                }
                .wp-block-button__link {
                    display: inline-block;
                    background-color: #d32f2f;
                    color: #ffffff !important;
                    padding: 10px 20px;
                    border-radius: 6px;
                    text-decoration: none;
                    font-weight: 600;
                    margin: 8px 0;
                }
            </style>
        </head>
        <body>
            $htmlContent
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        factory = { context ->
            WebView(context).apply {
                setBackgroundColor(Color.TRANSPARENT)
                settings.apply {
                    javaScriptEnabled = false
                    defaultTextEncodingName = "utf-8"
                    domStorageEnabled = false
                    loadWithOverviewMode = true
                    useWideViewPort = false
                    cacheMode = WebSettings.LOAD_DEFAULT
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url?.toString() ?: return false
                        if (onLinkClicked != null) {
                            onLinkClicked(url)
                        } else {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        return true
                    }
                }
                loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, styledHtml, "text/html", "UTF-8", null)
        }
    )
}
