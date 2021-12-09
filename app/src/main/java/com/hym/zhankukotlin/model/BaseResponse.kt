package com.hym.zhankukotlin.model

/**
 * @author hehua2008
 * @date 2021/12/7
 */
abstract class BaseResponse<T> {
    abstract val code: Int

    abstract val dataContent: T?

    abstract val msg: String
}