package com.hym.zhankukotlin.ui

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.target.ViewTarget

object ImageViewHeightListener : RequestListener<Drawable> {
    override fun onLoadFailed(
            e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
    ): Boolean {
        return false
    }

    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                 dataSource: DataSource?, isFirstResource: Boolean
    ): Boolean {
        val bitmap = when (resource) {
            is BitmapDrawable -> resource.bitmap
            is GifDrawable -> resource.firstFrame
            else -> null
        } ?: return false
        if (target is ViewTarget<*, *>) {
            val viewWidth = target.view.width
            if (viewWidth > 0) {
                val lp = target.view.layoutParams
                lp.height = viewWidth * bitmap.height / bitmap.width
                target.view.layoutParams = lp
            }
        }
        return false
    }
}