package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
 * @author hehua2008
 * @date 2021/12/7
 */
@Serializable
abstract class BaseResponse<T> {
    abstract val code: Int

    abstract val dataContent: T?

    abstract val msg: String
}