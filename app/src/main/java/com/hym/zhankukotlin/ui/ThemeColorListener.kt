package com.hym.zhankukotlin.ui

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.util.MMCQ
import com.hym.zhankukotlin.util.ViewUtils.getActivityContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object ThemeColorListener : RequestListener<Drawable> {
    override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
    ): Boolean {
        return false
    }

    override fun onResourceReady(
            resource: Drawable?, model: Any?, target: Target<Drawable>?,
            dataSource: DataSource?, isFirstResource: Boolean
    ): Boolean {
        if (resource is BitmapDrawable && target is ViewTarget<*, *>) {
            val activity = target.view.getActivityContext() as? ComponentActivity
            activity?.lifecycleScope?.launch(Dispatchers.Main) {
                val toolbar = activity.findViewById(R.id.action_bar) as? Toolbar ?: return@launch
                val themeColors: List<MMCQ.ThemeColor>
                withContext(Dispatchers.Default) {
                    val mmcq = MMCQ(resource.bitmap, 2)
                    themeColors = mmcq.quantize()
                }
                if (themeColors.isEmpty()) return@launch
                val mainThemeColor = themeColors[0]
                toolbar.setBackgroundColor(mainThemeColor.color)
                toolbar.setTitleTextColor(mainThemeColor.titleTextColor)
                toolbar.setSubtitleTextColor(mainThemeColor.titleTextColor)
                activity.window.statusBarColor = mainThemeColor.color
            }
        }
        return false
    }
}