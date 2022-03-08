package com.hym.zhankukotlin.util

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import java.lang.reflect.Method

private val generateDefaultLayoutParamsMethod: Method =
    ViewGroup::class.java.getDeclaredMethod("generateDefaultLayoutParams").apply {
        isAccessible = true
    }

fun ViewGroup.createDefaultLayoutParams(): ViewGroup.LayoutParams {
    return generateDefaultLayoutParamsMethod.invoke(this) as ViewGroup.LayoutParams
}

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

fun View.requireActivity(): Activity {
    return getActivity() ?: throw IllegalStateException("View $this not attached to a context.")
}
