package com.hym.zhankumultiplatform.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.hym.zhankumultiplatform.util.getActivity

/**
 * @author hehua2008
 * @date 2024/7/7
 */
@Composable
actual fun updateStatusBarColor(darkTheme: Boolean, isInit: Boolean) {
    val view = LocalView.current
    val window = view.getActivity()?.window ?: return

    val initial = remember {
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars
    }

    DisposableEffect(darkTheme) {
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme

        onDispose {
            if (isInit) return@onDispose
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = initial
        }
    }
}
