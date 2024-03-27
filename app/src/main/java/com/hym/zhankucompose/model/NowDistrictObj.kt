package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

@Serializable
class NowDistrictObj : NowAddressObj() {
    override val id: Int = -1
    override val level: Int = -1
    override val name: String = ""
    override val nameEn: String = ""
    override val parent: Int = -1
}