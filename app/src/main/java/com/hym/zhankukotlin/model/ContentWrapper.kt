package com.hym.zhankukotlin.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
{
"object": {...},
"objectType": 78
}
 */
@Keep
data class ContentWrapper(
    @SerializedName("object")
    val content: Content,
    val objectType: Int
)