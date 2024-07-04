package com.hym.zhankucompose.navigation

/**
 * @author hehua2008
 * @date 2024/7/3
 */
interface NavListener {
    fun onNavigateToDetails(arguments: DetailsArgs)

    fun onNavigateToTagList(arguments: TagListArgs)

    fun onNavigateToImagePager(arguments: ImagePagerArgs)

    fun onNavigateToWebView(arguments: WebViewArgs)
}
