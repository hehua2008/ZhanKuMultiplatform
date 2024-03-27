package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"content": [...],
"first": false,
"last": true,
"loadNumber": 1,
"number": 1,
"numberOfElements": 0,
"pageable": {...},
"size": 10,
"total": 0,
"totalContent": 0,
"totalElements": 0,
"totalPages": 0
}
 */
@Serializable
data class SearchContent(
    val content: List<ContentWrapper>,
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