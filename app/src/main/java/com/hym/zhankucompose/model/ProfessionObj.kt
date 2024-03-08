package com.hym.zhankucompose.model

import androidx.annotation.Keep

/**
{
"id": 23,
"name": "教育工作者",
"nameEn": "Educators"
}
 */
@Keep
data class ProfessionObj(
    val id: Int,
    val name: String,
    val nameEn: String
)