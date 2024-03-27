package com.hym.zhankucompose.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GET https://api.zcool.com.cn/v2/api/topCate?app=android
 * GET https://api.zcool.com.cn/v2/api/getAllCategoryListContainArticle.do?app=android
 */
@Serializable
data class TopCateResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: List<TopCate>?,

    override val msg: String
) : BaseResponse<List<TopCate>>()