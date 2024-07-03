package com.hym.zhankucompose.ui.webview

import android.content.Intent
import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.ui.NestedWebView
import com.hym.zhankucompose.ui.theme.ComposeTheme

/**
 * @author hehua2008
 * @date 2024/7/3
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebScreen(initialUrl: String, initialTitle: String = "") {
    ComposeTheme {
        val navController = LocalNavController.current
        val density = LocalDensity.current
        val systemBarsTop = WindowInsets.systemBars.getTop(density)
        val topAppBarHeight = remember(density, systemBarsTop) {
            with(density) { systemBarsTop.toDp() } + 36.dp
        }
        var barTitle by remember { mutableStateOf(initialTitle) }
        var onBackClick: () -> Boolean by remember { mutableStateOf({ false }) }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.height(topAppBarHeight),
                    title = {
                        Box(modifier = Modifier.fillMaxHeight()) {
                            Text(
                                text = barTitle,
                                modifier = Modifier.align(Alignment.CenterStart),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.vector_arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    if (!onBackClick()) {
                                        navController.popBackStack()
                                    }
                                }
                                .fillMaxHeight()
                                .padding(horizontal = 12.dp)
                        )
                    }
                )
            }
        ) { innerPadding ->
            var isStatusVisible by remember { mutableStateOf(false) }
            var progress by remember { mutableIntStateOf(0) }
            val pullRefreshState = rememberPullToRefreshState()
            var render: () -> Unit by remember { mutableStateOf(EMPTY_BLOCK) }

            LaunchedEffect(pullRefreshState) {
                snapshotFlow { pullRefreshState.isRefreshing }
                    .collect {
                        if (it) {
                            render()
                        }
                    }
            }

            if (!isStatusVisible) {
                pullRefreshState.endRefresh()
            }

            // innerPadding contains inset information for you to use and apply
            Box(
                modifier = Modifier
                    // consume insets as scaffold doesn't do it by default
                    .padding(innerPadding)
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) {
                var currentUrl by remember { mutableStateOf(initialUrl) }

                AndroidView(
                    factory = { context ->
                        NestedWebView(context).also { webView ->
                            onBackClick = {
                                if (webView.canGoBack()) {
                                    webView.goBack()
                                    true
                                } else {
                                    false
                                }
                            }

                            render = {
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
                                    isStatusVisible = true
                                    //customWebViewClient?.onPageStarted(view, url, favicon)
                                }

                                override fun onPageFinished(view: WebView, url: String) {
                                    isStatusVisible = false
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
                                    barTitle = title ?: ""
                                    //customWebChromeClient?.onReceivedTitle(view, title)
                                }

                                override fun onProgressChanged(view: WebView, newProgress: Int) {
                                    progress = newProgress
                                    //customWebChromeClient?.onProgressChanged(view, newProgress)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    render()
                }

                if (isStatusVisible) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }

                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
