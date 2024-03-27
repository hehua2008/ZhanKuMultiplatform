package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"createTime": 1389888000000,
"dr": 1,
"id": 1,
"name": "平面设计师",
"orderno": 1,
"postGroup": 1
}
 */
@Serializable
data class PostMap(
    val createTime: Long,
    val dr: Int,
    val id: Int,
    val name: String,
    val orderno: Int,
    val postGroup: Int
)