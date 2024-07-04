package com.hym.zhankucompose.navigation

import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.photo.UrlPhotoInfo
import kotlinx.collections.immutable.ImmutableList

/**
 * @author hehua2008
 * @date 2024/7/4
 */
interface NavArgs

data class DetailsArgs(
    val contentType: ContentType,
    val contentId: String
) : NavArgs

data class TagListArgs(
    val author: CreatorObj?,
    val topCate: TopCate?,
    val subCate: SubCate?
) : NavArgs

data class ImagePagerArgs(
    val photoInfos: ImmutableList<UrlPhotoInfo>,
    val currentPosition: Int
) : NavArgs

data class WebViewArgs(
    val url: String,
    val title: String
) : NavArgs
