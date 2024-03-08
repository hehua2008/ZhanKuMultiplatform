package com.hym.zhankucompose.model

import androidx.annotation.Keep

/**
{
"limit": 10,
"loadIndex": 1,
"loadNumber": 1,
"loadSize": 10,
"offset": 0,
"pageIndex": 1,
"pageNumber": 1,
"pageSize": 10
}
 */
@Keep
data class Pageable(
    val limit: Int,
    val loadIndex: Int,
    val loadNumber: Int,
    val loadSize: Int,
    val offset: Int,
    val pageIndex: Int,
    val pageNumber: Int,
    val pageSize: Int
)