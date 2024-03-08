package com.hym.zhankucompose.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * 设计师
 * GET https://api.zcool.com.cn/v2/api/search/designer/v3?app=android&p=1&ps=10&word=
 */
@Keep
data class SearchDesignerResponse(
    override val code: Int,

    @SerializedName("data")
    override val dataContent: SearchDesigner?,

    override val msg: String
) : BaseResponse<SearchDesigner>()