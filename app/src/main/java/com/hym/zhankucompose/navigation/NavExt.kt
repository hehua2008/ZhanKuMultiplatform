package com.hym.zhankucompose.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

/**
 * @author hehua2008
 * @date 2024/7/2
 */
val LocalNavController = staticCompositionLocalOf<NavController> {
    error("LocalNavController not present")
}

val LocalNavListener = staticCompositionLocalOf<NavListener> {
    error("LocalNavListener not present")
}
