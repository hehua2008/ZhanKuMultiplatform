package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"id": 9,
"image": "https://static.zcool.cn/z/images/svg/honor_authen.svg",
"name": "站酷认证",
"nameEn": "",
"status": 1,
"url": "https://www.zcool.com.cn/company/about",
"description": "站酷认证团队",
"sort": 7
}
 */
@Serializable
data class MemberHonor(
    val id: Int,
    val image: String,
    val name: String,
    val nameEn: String,
    val status: Int,
    val url: String
) {
    val description: String = ""

    val sort: Int = 0
}