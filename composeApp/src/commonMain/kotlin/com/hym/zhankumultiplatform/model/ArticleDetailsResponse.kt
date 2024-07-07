package com.hym.zhankumultiplatform.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDetailsResponse(
    override val code: Int,

    @SerialName("data")
    override val dataContent: ArticleDetails?,

    override val msg: String
) : BaseResponse<ArticleDetails>()