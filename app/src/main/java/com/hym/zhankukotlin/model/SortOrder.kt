package com.hym.zhankukotlin.model

enum class SortOrder(val value: Int) {
    LATEST_PUBLISH(8), MOST_RECOMMEND(1), MOST_FAVORITE(4), MOST_COMMENT(3);

    override fun toString(): String = value.toString()
}