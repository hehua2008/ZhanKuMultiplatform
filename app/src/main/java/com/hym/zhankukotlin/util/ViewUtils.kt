package com.hym.zhankukotlin.util

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import java.lang.reflect.Method

object ViewUtils {
    private val mGenerateDefaultLayoutParamsMethod: Method =
        ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams").apply {
            isAccessible = true
        }

    @JvmStatic
    fun View.getDefaultLayoutParamsFrom(parent: ViewGroup): ViewGroup.LayoutParams {
        return mGenerateDefaultLayoutParamsMethod.invoke(parent) as ViewGroup.LayoutParams
    }

    @JvmStatic
    fun View.getActivity(): Activity? {
        var view = this
        while (true) {
            val ctx = view.context
            if (ctx is Activity) return ctx
            val parent = view.parent
            if (parent is View) {
                view = parent
            } else {
                return null
            }
        }
    }

    @JvmStatic
    fun View.requireActivity(): Activity {
        return getActivity() ?: throw IllegalStateException("View $this not attached to a context.")
    }
}