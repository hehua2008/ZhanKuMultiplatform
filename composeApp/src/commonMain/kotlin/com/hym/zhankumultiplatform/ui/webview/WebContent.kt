package com.hym.zhankumultiplatform.ui.webview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import kotlinx.coroutines.launch

/**
 * @author huahu2008
 * @date 2024/7/3
 */
@Composable
fun WebViewContent(
    initialUrl: String,
    updateStatusVisibility: (Boolean) -> Unit,
    updateTitle: (String) -> Unit,
    updateProgress: (Int) -> Unit,
    setOnBackClick: (() -> Boolean) -> Unit,
    setOnRefreshing: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    //val webState = rememberSaveableWebViewState(url = initialUrl) // bug
    val webState = rememberWebViewState(url = initialUrl)
    val webViewNavigator = rememberWebViewNavigator()

    LaunchedEffect(webState, webViewNavigator) {
        setOnBackClick {
            if (webViewNavigator.canGoBack) {
                webViewNavigator.navigateBack()
                true
            } else {
                false
            }
        }

        setOnRefreshing {
            webViewNavigator.reload()
        }

        launch {
            snapshotFlow { webState.pageTitle }
                .collect {
                    // Bug: webState.pageTitle will be null after reloading
                    if (it.isNullOrBlank()) return@collect
                    updateTitle(it)
                }
        }

        launch {
            snapshotFlow { webState.loadingState }
                .collect {
                    if (it is LoadingState.Loading) {
                        updateStatusVisibility(true)
                        updateProgress((it.progress * 100).toInt())
                    } else if (it is LoadingState.Finished) {
                        updateStatusVisibility(false)
                    }
                }
        }
    }

    WebView(
        state = webState,
        modifier = modifier,
        captureBackPresses = true,
        navigator = webViewNavigator
    )
}
