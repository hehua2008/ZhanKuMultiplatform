package com.hym.zhankukotlin.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * 首页推荐
 * GET https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&activity=0&recommendLevel=3&contentType=0
 *
 * 个人作品
 * GET https://api.zcool.com.cn/v2/api/u/601779?app=android&p=1&ps=10&sort=8
 */
@Keep
data class ContentPageResponse(
    override val code: Int,

    @SerializedName("data")
    override val dataContent: ContentPage?,

    override val msg: String
) : BaseResponse<ContentPage>()