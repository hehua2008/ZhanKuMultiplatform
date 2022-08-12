package com.hym.zhankukotlin.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ArticleDetailsResponse(
    override val code: Int,

    @SerializedName("data")
    override val dataContent: ArticleDetails?,

    override val msg: String
) : BaseResponse<ArticleDetails>()