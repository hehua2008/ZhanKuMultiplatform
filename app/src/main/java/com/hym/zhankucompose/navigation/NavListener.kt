package com.hym.zhankucompose.navigation

import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.photo.UrlPhotoInfo
import kotlinx.collections.immutable.ImmutableList

/**
 * @author hehua2008
 * @date 2024/7/3
 */
interface NavListener {
    fun onNavigateToDetails(contentType: ContentType, contentId: String)

    fun onNavigateToTagList(author: CreatorObj?, topCate: TopCate?, subCate: SubCate?)

    fun onNavigateToImagePager(photoInfos: ImmutableList<UrlPhotoInfo>, currentPosition: Int)

    fun onNavigateToWebView(url: String, title: String)
}
