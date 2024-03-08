package com.hym.zhankucompose.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.hym.zhankucompose.R
import com.hym.zhankucompose.util.MMCQ
import com.hym.zhankucompose.util.getActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ThemeColorRetriever : RequestListener<Drawable> {
    override fun onLoadFailed(
        e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean
    ): Boolean = false

    override fun onResourceReady(
        resource: Drawable, model: Any, target: Target<Drawable>?,
        dataSource: DataSource, isFirstResource: Boolean
    ): Boolean {
        if (target is ViewTarget<*, *>) {
            val bitmap = when (resource) {
                is BitmapDrawable -> resource.bitmap
                is GifDrawable -> resource.firstFrame
                else -> null
            } ?: return false
            val activity = target.view.getActivity() ?: return false
            if (activity !is LifecycleOwner) return false
            activity.lifecycleScope.launch(Dispatchers.Main) {
                activity.setThemeColor(getMainThemeColor(bitmap))
            }
        }
        return false
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
        val toolbar = findViewById(R.id.action_bar) as? Toolbar ?: return
        toolbar.setBackgroundColor(themeColor.color)
        toolbar.setTitleTextColor(themeColor.titleTextColor)
        toolbar.setSubtitleTextColor(themeColor.titleTextColor)
        window.statusBarColor = themeColor.color
    }
}