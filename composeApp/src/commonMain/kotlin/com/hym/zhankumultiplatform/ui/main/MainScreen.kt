package com.hym.zhankumultiplatform.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hym.zhankumultiplatform.navigation.DetailsArgs
import com.hym.zhankumultiplatform.navigation.ImagePagerArgs
import com.hym.zhankumultiplatform.navigation.LocalNavController
import com.hym.zhankumultiplatform.navigation.LocalNavListener
import com.hym.zhankumultiplatform.navigation.NavArgsViewModel
import com.hym.zhankumultiplatform.navigation.NavListener
import com.hym.zhankumultiplatform.navigation.Route
import com.hym.zhankumultiplatform.navigation.TagListArgs
import com.hym.zhankumultiplatform.navigation.WebViewArgs
import com.hym.zhankumultiplatform.navigation.getArgs
import com.hym.zhankumultiplatform.ui.detail.DetailScreen
import com.hym.zhankumultiplatform.ui.imagepager.ZoomImagePagerScreen
import com.hym.zhankumultiplatform.ui.tag.TagScreen
import com.hym.zhankumultiplatform.ui.webview.WebScreen

/**
 * @author hehua2008
 * @date 2024/7/4
 */
@Composable
fun MainScreen() {
    val navArgsViewModel = viewModel<NavArgsViewModel> { NavArgsViewModel() }
    val navController = rememberNavController()
    val navListener = remember(navArgsViewModel, navController) {
        object : NavListener {
            override fun onNavigateToDetails(arguments: DetailsArgs) {
                navArgsViewModel.putArgs(arguments)
                navController.navigate(route = Route.Details.path)
            }

            override fun onNavigateToTagList(arguments: TagListArgs) {
                navArgsViewModel.putArgs(arguments)
                navController.navigate(route = Route.TagList.path)
            }

            override fun onNavigateToImagePager(arguments: ImagePagerArgs) {
                navArgsViewModel.putArgs(arguments)
                navController.navigate(route = Route.ImagePager.path)
            }

            override fun onNavigateToWebView(arguments: WebViewArgs) {
                navArgsViewModel.putArgs(arguments)
                navController.navigate(route = Route.WebView.path)
            }
        }
    }

    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalNavListener provides navListener
    ) {
        NavHost(navController, startDestination = Route.Home.path) {
            composable(route = Route.Home.path) { backStackEntry ->
                HomeScreen()
            }

            composable(route = Route.Details.path) { backStackEntry ->
                val arguments: DetailsArgs = navArgsViewModel.getArgs(backStackEntry.id)
                DetailScreen(
                    contentType = arguments.contentType,
                    contentId = arguments.contentId
                )
            }

            composable(route = Route.TagList.path) { backStackEntry ->
                val arguments: TagListArgs = navArgsViewModel.getArgs(backStackEntry.id)
                TagScreen(
                    author = arguments.author,
                    topCate = arguments.topCate,
                    subCate = arguments.subCate
                )
            }

            composable(route = Route.ImagePager.path) { backStackEntry ->
                val arguments: ImagePagerArgs = navArgsViewModel.getArgs(backStackEntry.id)
                ZoomImagePagerScreen(
                    photoInfoList = arguments.photoInfos,
                    initialIndex = arguments.currentPosition
                )
            }

            composable(route = Route.WebView.path) { backStackEntry ->
                val arguments: WebViewArgs = navArgsViewModel.getArgs(backStackEntry.id)
                WebScreen(
                    initialUrl = arguments.url,
                    initialTitle = arguments.title
                )
            }
        }
    }
}
