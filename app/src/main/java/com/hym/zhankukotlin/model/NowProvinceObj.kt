package com.hym.zhankukotlin.model

import androidx.annotation.Keep

/**
{
"id": 247,
"level": 3,
"name": "北京",
"nameEn": "Beijing",
"parent": 7
}
 */
@Keep
data class NowProvinceObj(
    override val id: Int,
    override val level: Int,
    override val name: String,
    override val nameEn: String,
    override val parent: Int
) : NowAddressObj()