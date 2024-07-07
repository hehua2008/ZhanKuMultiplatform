package com.hym.zhankumultiplatform.util

import kotlin.experimental.ExperimentalNativeApi

/**
 * @author hehua2008
 * @date 2024/7/4
 */
@OptIn(ExperimentalNativeApi::class)
actual typealias WeakReference<T> = kotlin.native.ref.WeakReference<T>
