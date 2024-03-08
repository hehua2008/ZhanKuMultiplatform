package com.hym.zhankucompose.model

import androidx.annotation.Keep

/**
{
"id": 4170,
"level": 4,
"name": "北京",
"nameEn": "Beijing",
"parent": 247
}
 */
@Keep
data class NowCityObj(
    override val id: Int,
    override val level: Int,
    override val name: String,
    override val nameEn: String,
    override val parent: Int
) : NowAddressObj()