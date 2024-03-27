package com.hym.zhankucompose.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 设计师
 * GET https://api.zcool.com.cn/v2/api/search/designer/v3?app=android&p=1&ps=10&word=
 */
@Serializable
data class SearchDesignerResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: SearchDesigner?,

    override val msg: String
) : BaseResponse<SearchDesigner>()