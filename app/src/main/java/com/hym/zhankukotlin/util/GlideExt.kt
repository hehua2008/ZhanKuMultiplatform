package com.hym.zhankukotlin.util

import com.bumptech.glide.load.engine.executor.GlideExecutor
import java.util.concurrent.ExecutorService

/**
 * @author hehua2008
 * @date 2022/3/31
 */

private val glideExecutorDelegateField =
    GlideExecutor::class.java.getDeclaredField("delegate").apply {
        isAccessible = true
    }

fun GlideExecutor.setIdleExecutorService(): GlideExecutor {
    val oriDelegate = glideExecutorDelegateField.get(this) as ExecutorService
    val newDelegate = IdleExecutorService(oriDelegate)
    glideExecutorDelegateField.set(this, newDelegate)
    return this
}