package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"id": 247,
"level": 3,
"name": "北京",
"nameEn": "Beijing",
"parent": 7
}
 */
@Serializable
data class NowProvinceObj(
    override val id: Int,
    override val level: Int,
    override val name: String,
    override val nameEn: String,
    override val parent: Int
) : NowAddressObj()