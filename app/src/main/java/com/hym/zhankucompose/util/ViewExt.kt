package com.hym.zhankucompose.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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

fun View.copyText(): Boolean {
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)!!
    val text: CharSequence = if (this is TextView) text else contentDescription ?: return false
    val clipData = ClipData.newPlainText(null, text)
    clipboard.setPrimaryClip(clipData)
    Toast.makeText(context, "Copied: $text", Toast.LENGTH_SHORT).show()
    return true
}
