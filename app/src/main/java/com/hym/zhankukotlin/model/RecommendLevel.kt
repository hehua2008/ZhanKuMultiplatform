package com.hym.zhankukotlin.model

enum class RecommendLevel(override val value: Int, override val text: String) :
    OrderEnum<RecommendLevel> {
    ALL_RECOMMEND(1, "全部推荐"),
    EDITOR_CHOICE(2, "编辑精选"),
    HOME_RECOMMEND(3, "首页推荐"),
    LATEST_PUBLISH(-1, "最新发布");

    override fun toString(): String = value.toString()
}