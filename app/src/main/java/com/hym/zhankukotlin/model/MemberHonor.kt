package com.hym.zhankukotlin.model

import androidx.annotation.Keep

/**
{
"id": 9,
"image": "https://static.zcool.cn/z/images/svg/honor_authen.svg",
"name": "站酷认证",
"nameEn": "",
"status": 1,
"url": "https://www.zcool.com.cn/company/about"
}
 */
@Keep
data class MemberHonor(
    val id: Int,
    val image: String,
    val name: String,
    val nameEn: String,
    val status: Int,
    val url: String
)