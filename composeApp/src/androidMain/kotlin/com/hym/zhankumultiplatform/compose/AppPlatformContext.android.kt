package com.hym.zhankumultiplatform.compose

import coil3.PlatformContext
import com.hym.zhankumultiplatform.MyApplication

/**
 * @author hehua2008
 * @date 2024/7/6
 */
actual val AppPlatformContext: PlatformContext
    get() = MyApplication.INSTANCE
