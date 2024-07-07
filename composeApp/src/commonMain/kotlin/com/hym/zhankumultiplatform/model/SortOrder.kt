package com.hym.zhankumultiplatform.model

enum class SortOrder(override val value: Int, override val text: String) :
    OrderEnum<SortOrder> {
    LATEST_PUBLISH(8, "时间最新"),
    MOST_RECOMMEND(1, "推荐最多"),
    MOST_FAVORITE(4, "收藏最多"),
    MOST_COMMENT(3, "评论最多"),
    BEST_MATCH(5, "最佳匹配");

    override fun toString(): String = value.toString()
}