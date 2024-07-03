package com.hym.zhankucompose.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.navigation.Route
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

    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(navController, startDestination = Route.Home.path) {
            composable(route = Route.Home.path) { backStackEntry ->
                HomeScreen(
                    onNavigateToDetails = { contentType, contentId ->
                        mainViewModel.contentType = contentType
                        mainViewModel.contentId = contentId
                        navController.navigate(route = Route.Details.path)
                    },
                    onNavigateToTagList = { author, topCate, subCate ->
                        mainViewModel.author = author
                        mainViewModel.topCate = topCate
                        mainViewModel.subCate = subCate
                        navController.navigate(route = Route.TagList.path)
                    },
                    onNavigateToWebView = { url, title ->
                        mainViewModel.webUrl = url
                        mainViewModel.webTitle = title
                        navController.navigate(route = Route.WebView.path)
                    }
                )
            }

            composable(route = Route.Details.path) { backStackEntry ->
                DetailScreen(
                    contentType = mainViewModel.contentType,
                    contentId = mainViewModel.contentId,
                    onNavigateToTagList = { author, topCate, subCate ->
                        mainViewModel.author = author
                        mainViewModel.topCate = topCate
                        mainViewModel.subCate = subCate
                        navController.navigate(route = Route.TagList.path)
                    },
                    onNavigateToImagePager = { photoInfos, currentPosition ->
                        mainViewModel.photoInfos = photoInfos
                        mainViewModel.currentPosition = currentPosition
                        navController.navigate(route = Route.ImagePager.path)
                    },
                    onNavigateToWebView = { url, title ->
                        mainViewModel.webUrl = url
                        mainViewModel.webTitle = title
                        navController.navigate(route = Route.WebView.path)
                    }
                )
            }

            composable(route = Route.TagList.path) { backStackEntry ->
                TagScreen(
                    author = mainViewModel.author,
                    topCate = mainViewModel.topCate,
                    subCate = mainViewModel.subCate,
                    onNavigateToDetails = { contentType, contentId ->
                        mainViewModel.contentType = contentType
                        mainViewModel.contentId = contentId
                        navController.navigate(route = Route.Details.path)
                    },
                    onNavigateToTagList = { author, topCate, subCate ->
                        mainViewModel.author = author
                        mainViewModel.topCate = topCate
                        mainViewModel.subCate = subCate
                        navController.navigate(route = Route.TagList.path)
                    },
                    onNavigateToWebView = { url, title ->
                        mainViewModel.webUrl = url
                        mainViewModel.webTitle = title
                        navController.navigate(route = Route.WebView.path)
                    }
                )
            }

            composable(route = Route.ImagePager.path) { backStackEntry ->
                ZoomImagePagerScreen(
                    photoInfoList = mainViewModel.photoInfos,
                    initialIndex = mainViewModel.currentPosition
                )
            }

            composable(route = Route.WebView.path) { backStackEntry ->
                WebScreen(
                    initialUrl = mainViewModel.webUrl,
                    initialTitle = mainViewModel.webTitle
                )
            }
        }
    }
}
