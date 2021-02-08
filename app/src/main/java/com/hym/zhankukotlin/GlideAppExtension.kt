package com.hym.zhankukotlin

import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory

@GlideExtension
object GlideAppExtension {
    // Size of mini thumb in pixels.
    private const val MINI_THUMB_SIZE = 100

    @JvmField
    val DRAWABLE_CROSS_FADE = DrawableTransitionOptions.withCrossFade(
        DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(false).build()
    )

    @JvmStatic
    @GlideOption
    fun miniThumb(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.fitCenter().override(MINI_THUMB_SIZE)
    }

    @JvmStatic
    @GlideOption
    fun transparentPlaceHolder(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.placeholder(MyApplication.transparentDrawable)
    }

    @JvmStatic
    @GlideOption
    fun originalSize(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.override(Target.SIZE_ORIGINAL)
    }
}