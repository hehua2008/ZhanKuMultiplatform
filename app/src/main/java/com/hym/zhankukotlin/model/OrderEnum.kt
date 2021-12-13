package com.hym.zhankukotlin.model

/**
 * @author hehua2008
 * @date 2021/12/13
 */
interface OrderEnum<E> where E : Enum<E>, E : OrderEnum<E> {
    val value: Int
    val text: String
}