package com.hym.zhankucompose.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hym.zhankucompose.navigation.DetailsArgs
import com.hym.zhankucompose.navigation.ImagePagerArgs
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.navigation.LocalNavListener
import com.hym.zhankucompose.navigation.NavListener
import com.hym.zhankucompose.navigation.Route
import com.hym.zhankucompose.navigation.TagListArgs
import com.hym.zhankucompose.navigation.WebViewArgs
import com.hym.zhankucompose.ui.detail.DetailScreen
import com.hym.zhankucompose.ui.imagepager.ZoomImagePagerScreen
import com.hym.zhankucompose.ui.tag.TagScreen
import com.hym.zhankucompose.ui.webview.WebScreen

/**
 * @author hehua2008
 * @date 2024/7/4
 */
@Composable
fun MainScreen() {
    val mainViewModel = viewModel<MainViewModel>()
    val navController = rememberNavController()
    val navListener = remember(mainViewModel, navController) {
        object : NavListener {
            override fun onNavigateToDetails(arguments: DetailsArgs) {
                mainViewModel.detailsArguments = arguments
                navController.navigate(route = Route.Details.path)
            }

            override fun onNavigateToTagList(arguments: TagListArgs) {
                mainViewModel.tagListArguments = arguments
                navController.navigate(route = Route.TagList.path)
            }

            override fun onNavigateToImagePager(arguments: ImagePagerArgs) {
                mainViewModel.imagePagerArguments = arguments
                navController.navigate(route = Route.ImagePager.path)
            }

            override fun onNavigateToWebView(arguments: WebViewArgs) {
                mainViewModel.webViewArguments = arguments
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
                val arguments = remember(mainViewModel, backStackEntry.id) {
                    var args =
                        mainViewModel.argumentsMap[backStackEntry.id] as DetailsArgs?
                    if (args == null) {
                        args = mainViewModel.detailsArguments!!
                        mainViewModel.argumentsMap[backStackEntry.id] = args
                    }
                    args
                }
                DetailScreen(
                    contentType = arguments.contentType,
                    contentId = arguments.contentId
                )
            }

            composable(route = Route.TagList.path) { backStackEntry ->
                val arguments = remember(mainViewModel, backStackEntry.id) {
                    var args =
                        mainViewModel.argumentsMap[backStackEntry.id] as TagListArgs?
                    if (args == null) {
                        args = mainViewModel.tagListArguments!!
                        mainViewModel.argumentsMap[backStackEntry.id] = args
                    }
                    args
                }
                TagScreen(
                    author = arguments.author,
                    topCate = arguments.topCate,
                    subCate = arguments.subCate
                )
            }

            composable(route = Route.ImagePager.path) { backStackEntry ->
                val arguments = remember(mainViewModel, backStackEntry.id) {
                    var args =
                        mainViewModel.argumentsMap[backStackEntry.id] as ImagePagerArgs?
                    if (args == null) {
                        args = mainViewModel.imagePagerArguments!!
                        mainViewModel.argumentsMap[backStackEntry.id] = args
                    }
                    args
                }
                ZoomImagePagerScreen(
                    photoInfoList = arguments.photoInfos,
                    initialIndex = arguments.currentPosition
                )
            }

            composable(route = Route.WebView.path) { backStackEntry ->
                val arguments = remember(mainViewModel, backStackEntry.id) {
                    var args =
                        mainViewModel.argumentsMap[backStackEntry.id] as WebViewArgs?
                    if (args == null) {
                        args = mainViewModel.webViewArguments!!
                        mainViewModel.argumentsMap[backStackEntry.id] = args
                    }
                    args
                }
                WebScreen(
                    initialUrl = arguments.url,
                    initialTitle = arguments.title
                )
            }
        }
    }
}
