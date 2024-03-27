package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"id": 5,
"image": "https://static.zcool.cn/z/images/svg/honor_tuijian_designer.svg",
"name": "站酷推荐设计师",
"nameEn": "",
"status": 1,
"url": "https://www.zcool.com.cn/toDesigners.do"
}
 */
@Serializable
data class Honor(
    val id: Int,
    val image: String,
    val name: String,
    val nameEn: String,
    val status: Int,
    val url: String
)