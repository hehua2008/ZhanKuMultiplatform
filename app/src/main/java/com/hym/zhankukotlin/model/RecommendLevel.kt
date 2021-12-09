package com.hym.zhankukotlin.model

enum class RecommendLevel(val value: Int) {
    ALL_RECOMMEND(1), EDITOR_CHOICE(2), HOME_RECOMMEND(3), LATEST_PUBLISH(-1);

    override fun toString(): String = value.toString()
}