package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"id": 23,
"name": "教育工作者",
"nameEn": "Educators"
}
 */
@Serializable
data class ProfessionObj(
    val id: Int,
    val name: String,
    val nameEn: String
)