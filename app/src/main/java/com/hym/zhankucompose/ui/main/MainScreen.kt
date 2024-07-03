package com.hym.zhankucompose.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.navigation.LocalNavListener
import com.hym.zhankucompose.navigation.NavListener
import com.hym.zhankucompose.navigation.Route
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.ui.detail.DetailScreen
import com.hym.zhankucompose.ui.imagepager.ZoomImagePagerScreen
import com.hym.zhankucompose.ui.tag.TagScreen
import com.hym.zhankucompose.ui.webview.WebScreen
import kotlinx.collections.immutable.ImmutableList

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
            override fun onNavigateToDetails(
                contentType: ContentType,
                contentId: String
            ) {
                mainViewModel.contentType = contentType
                mainViewModel.contentId = contentId
                navController.navigate(route = Route.Details.path)
            }

            override fun onNavigateToTagList(
                author: CreatorObj?,
                topCate: TopCate?,
                subCate: SubCate?
            ) {
                mainViewModel.author = author
                mainViewModel.topCate = topCate
                mainViewModel.subCate = subCate
                navController.navigate(route = Route.TagList.path)
            }

            override fun onNavigateToImagePager(
                photoInfos: ImmutableList<UrlPhotoInfo>,
                currentPosition: Int
            ) {
                mainViewModel.photoInfos = photoInfos
                mainViewModel.currentPosition = currentPosition
                navController.navigate(route = Route.ImagePager.path)
            }

            override fun onNavigateToWebView(url: String, title: String) {
                mainViewModel.webUrl = url
                mainViewModel.webTitle = title
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
                DetailScreen(
                    contentType = mainViewModel.contentType,
                    contentId = mainViewModel.contentId
                )
            }

            composable(route = Route.TagList.path) { backStackEntry ->
                TagScreen(
                    author = mainViewModel.author,
                    topCate = mainViewModel.topCate,
                    subCate = mainViewModel.subCate
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
