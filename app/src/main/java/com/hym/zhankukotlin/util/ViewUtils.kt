package com.hym.zhankukotlin.util

import android.app.Activity
import android.content.Context
import android.view.View

object ViewUtils {
    @JvmStatic
    fun View.getActivityContext(): Context? {
        var view = this
        while (true) {
            val ctx = view.context
            if (ctx is Activity) {
                return ctx
            }
            val parent = view.parent
            if (parent is View) {
                view = parent
            } else {
                return null
            }
        }
    }
}