package com.hym.zhankumultiplatform.paging

data class LoadParamsHolder(
    val page: Int,
    val totalPages: Int = Int.MAX_VALUE,
    val lastId: Int? = null
) {
    companion object {
        val INITIAL = LoadParamsHolder(0)
    }

    operator fun plus(other: Int): LoadParamsHolder? {
        return (page + other).let {
            if (it > totalPages) null else copy(page = it, lastId = null)
        }
    }

    operator fun minus(other: Int): LoadParamsHolder? {
        return (page - other).let {
            if (it < 1) null else copy(page = it, lastId = null)
        }
    }
}