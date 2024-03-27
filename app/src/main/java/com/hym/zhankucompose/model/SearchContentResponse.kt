package com.hym.zhankucompose.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 综合 - 作品
 * GET https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&type=3&word=
 * 综合 - 文章
 * GET https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&type=8&word=
 * 作品
 * GET https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&field=0&recommendLevel=0&sort=5&type=3&word=
 * 文章
 * GET https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&field=0&recommendLevel=0&sort=5&type=8&word=
 */
@Serializable
data class SearchContentResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: SearchContent?,

    override val msg: String
) : BaseResponse<SearchContent>()