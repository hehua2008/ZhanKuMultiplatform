package com.hym.zhankumultiplatform.util

/**
 * @author hehua2008
 * @date 2024/7/4
 */
expect class WeakReference<T : Any>(referred: T) {
    fun get(): T?
}
