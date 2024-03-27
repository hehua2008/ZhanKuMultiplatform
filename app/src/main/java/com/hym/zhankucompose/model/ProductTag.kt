package com.hym.zhankucompose.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
{
"createTime": 1496734472000,
"id": 8607,
"name": "动画分镜",
"status": 1,
"updateTime": 1496734472000
}
 */
@Serializable
@Immutable
data class ProductTag(
    val createTime: Long,
    val id: Int,
    val name: String,
    val status: Int,
    val updateTime: Long
) {
    companion object {
        val Demo = ProductTag(
            createTime = 1496734472000,
            id = 8607,
            name = "动画分镜",
            status = 1,
            updateTime = 1496734472000
        )
    }
}