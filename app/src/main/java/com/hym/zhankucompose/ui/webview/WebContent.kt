package com.hym.zhankucompose.ui.webview

import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import com.hym.zhankucompose.R
import com.hym.zhankucompose.ui.NestedWebView

/**
 * @author huahu2008
 * @date 2024/7/3
 */
@Composable
fun WebContent(
    initialUrl: String,
    updateStatusVisibility: (Boolean) -> Unit,
    updateTitle: (String) -> Unit,
    updateProgress: (Int) -> Unit,
    setOnBackClick: (() -> Boolean) -> Unit,
    setOnRefreshing: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentUrl by remember { mutableStateOf(initialUrl) }

    AndroidView(
        factory = { context ->
            NestedWebView(context).also { webView ->
                setOnBackClick {
                    if (webView.canGoBack()) {
                        webView.goBack()
                        true
                    } else {
                        false
                    }
                }

                setOnRefreshing {
                    if (currentUrl.isNotBlank()) {
                        webView.loadUrl(currentUrl)
                    }
                }

                // Nested scrolling interop is enabled when
                // nested scroll is enabled for the root View
                ViewCompat.setNestedScrollingEnabled(webView, true)

                webView.settings.run {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }

                webView.webViewClient = object : WebViewClient() {
                    //val customWebViewClient = initWebViewClient()

                    override fun onPageStarted(
                        view: WebView,
                        url: String,
                        favicon: Bitmap?
                    ) {
                        currentUrl = url
                        updateStatusVisibility(true)
                        //customWebViewClient?.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        updateStatusVisibility(false)
                        //customWebViewClient?.onPageFinished(view, url)
                    }

                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest
                    ): Boolean {
                        val scheme = request.url.scheme
                        return when {
                            scheme == null
                                    || scheme.equals("http", true)
                                    || scheme.equals("https", true) -> {
                                false
                            }

                            else -> {
                                val intent = Intent(Intent.ACTION_VIEW, request.url)
                                val resolves =
                                    context.packageManager.queryIntentActivities(
                                        intent,
                                        0
                                    )
                                if (resolves.isNotEmpty()) {
                                    context.startActivity(
                                        Intent.createChooser(
                                            intent,
                                            context.getString(R.string.select_app_title)
                                        )
                                    )
                                }
                                true
                            }
                        }
                    }
                }

                webView.webChromeClient = object : WebChromeClient() {
                    //val customWebChromeClient = initWebChromeClient()

                    override fun onReceivedTitle(view: WebView, title: String?) {
                        updateTitle(title ?: "")
                        //customWebChromeClient?.onReceivedTitle(view, title)
                    }

                    override fun onProgressChanged(view: WebView, newProgress: Int) {
                        updateProgress(newProgress)
                        //customWebChromeClient?.onProgressChanged(view, newProgress)
                    }
                }
            }
        },
        modifier = modifier
    ) {
        if (currentUrl.isNotBlank()) {
            it.loadUrl(currentUrl)
        }
    }
}
