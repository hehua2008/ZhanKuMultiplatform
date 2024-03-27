package com.hym.zhankucompose.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 首页推荐
 * GET https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&activity=0&recommendLevel=3&contentType=0
 *
 * 个人作品
 * GET https://api.zcool.com.cn/v2/api/u/601779?app=android&p=1&ps=10&sort=8
 */
@Serializable
data class ContentPageResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: ContentPage?,

    override val msg: String
) : BaseResponse<ContentPage>()