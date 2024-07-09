package com.hym.zhankumultiplatform.ui.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

/**
 * @author huahu2008
 * @date 2024/7/3
 */

// TODO: init value for this
var createUIWebViewController: (
    initialUrl: String,
    updateStatusVisibility: (Boolean) -> Unit,
    updateTitle: (String) -> Unit,
    updateProgress: (Int) -> Unit,
    setOnBackClick: (() -> Boolean) -> Unit,
    setOnRefreshing: (() -> Unit) -> Unit
) -> UIViewController = { _, _, _, _, _, _ ->
    UIViewController()
}

@OptIn(ExperimentalForeignApi::class)
@Composable
fun WebContent(
    initialUrl: String,
    updateStatusVisibility: (Boolean) -> Unit,
    updateTitle: (String) -> Unit,
    updateProgress: (Int) -> Unit,
    setOnBackClick: (() -> Boolean) -> Unit,
    setOnRefreshing: (() -> Unit) -> Unit,
    modifier: Modifier
) {
    UIKitViewController(
        factory = {
            createUIWebViewController(
                initialUrl,
                updateStatusVisibility,
                updateTitle,
                updateProgress,
                setOnBackClick,
                setOnRefreshing
            )
        },
        modifier = modifier
    )
}
