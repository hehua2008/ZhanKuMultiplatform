package com.hym.zhankukotlin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author hehua2008
 * @date 2022/3/8
 */
@Parcelize
data class PhotoInfo(
    val url: String,
    val thumbUrl: String = url,
    val description: String = url,
    val width: Int = -1,
    val height: Int = -1
) : Parcelable
