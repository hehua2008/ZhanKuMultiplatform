package com.hym.zhankukotlin.model

/**
 * @author hehua2008
 * @date 2022/3/14
 */
enum class ContentType(override val value: Int, override val text: String) :
    OrderEnum<ContentType> {
    WORK(3, "作品"),
    ARTICLE(8, "文章");

    override fun toString(): String = value.toString()
}