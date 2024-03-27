package com.hym.zhankucompose.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
{
"object": {...},
"objectType": 78
}
 */
@Serializable
data class ContentWrapper(
    @SerialName("object")
    val content: Content,
    val objectType: Int
)