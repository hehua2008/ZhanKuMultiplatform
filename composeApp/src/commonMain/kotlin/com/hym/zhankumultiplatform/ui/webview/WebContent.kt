package com.hym.zhankumultiplatform.ui.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * @author huahu2008
 * @date 2024/7/3
 */
@Composable
expect fun WebContent(
    initialUrl: String,
    updateStatusVisibility: (Boolean) -> Unit,
    updateTitle: (String) -> Unit,
    updateProgress: (Int) -> Unit,
    setOnBackClick: (() -> Boolean) -> Unit,
    setOnRefreshing: (() -> Unit) -> Unit,
    modifier: Modifier = Modifier
)
