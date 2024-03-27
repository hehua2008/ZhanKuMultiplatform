package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"content": [...],
"first": false,
"last": false,
"loadNumber": 1,
"number": 1,
"numberOfElements": 10,
"pageable": {...},
"size": 10,
"total": 4210,
"totalContent": 0,
"totalElements": 4210,
"totalPages": 421
}
 */
@Serializable
data class ContentPage(
    val content: List<Content>,
    val first: Boolean,
    val last: Boolean,
    val loadNumber: Int,
    val number: Int,
    val numberOfElements: Int,
    val pageable: Pageable,
    val size: Int,
    val total: Int,
    val totalContent: Int,
    val totalElements: Int,
    val totalPages: Int
)