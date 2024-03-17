package com.hym.zhankucompose.ui

import android.app.Activity
import android.graphics.Bitmap
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.hym.zhankucompose.MyApplication
import com.hym.zhankucompose.R
import com.hym.zhankucompose.util.MMCQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThemeColorRetriever {
    suspend fun getMainThemeColor(model: Any): MMCQ.ThemeColor? {
        val requestManager = Glide.with(MyApplication.INSTANCE)
        val futureTarget = requestManager.asBitmap()
            .load(model)
            .submit(100, 100)
        return try {
            val bitmap = withContext(Dispatchers.IO) {
                futureTarget.get()
            }
            if (bitmap != null) getMainThemeColor(bitmap) else null
        } catch (e: Exception) {
            null
        } finally {
            requestManager.clear(futureTarget)
        }
    }

    suspend fun getMainThemeColor(bitmap: Bitmap): MMCQ.ThemeColor? {
        val themeColors = withContext(Dispatchers.Default) {
            val mmcq = MMCQ(bitmap, 3)
            mmcq.quantize()
        }
        return if (themeColors.isEmpty()) null else themeColors[0]
    }

    @JvmStatic
    fun Activity.setThemeColor(themeColor: MMCQ.ThemeColor?) {
        if (themeColor == null) return
        window.statusBarColor = themeColor.color
        val toolbar = findViewById(R.id.action_bar) as? Toolbar ?: return
        toolbar.setBackgroundColor(themeColor.color)
        toolbar.setTitleTextColor(themeColor.titleTextColor)
        toolbar.setSubtitleTextColor(themeColor.titleTextColor)
    }
}