package com.hym.zhankumultiplatform.paging

import androidx.annotation.MainThread

/**
 * @author hehua2008
 * @date 2022/3/28
 */
@MainThread
abstract class TotalPagesCallback {
    var totalPages: Int? = null
        private set

    fun invalidate() {
        totalPages = null
    }

    internal fun setTotalPages(totalPages: Int) {
        if (this.totalPages == totalPages) return
        this.totalPages = totalPages
        onUpdate(totalPages)
    }

    abstract fun onUpdate(totalPages: Int)
}