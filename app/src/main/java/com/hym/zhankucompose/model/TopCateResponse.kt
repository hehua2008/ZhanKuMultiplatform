package com.hym.zhankucompose.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * GET https://api.zcool.com.cn/v2/api/topCate?app=android
 * GET https://api.zcool.com.cn/v2/api/getAllCategoryListContainArticle.do?app=android
 */
@Keep
data class TopCateResponse(
    override val code: Int,

    @SerializedName("data")
    override val dataContent: List<TopCate>?,

    override val msg: String
) : BaseResponse<List<TopCate>>()