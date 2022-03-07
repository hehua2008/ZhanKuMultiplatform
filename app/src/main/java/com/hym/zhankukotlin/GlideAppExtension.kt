package com.hym.zhankukotlin

import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import jp.wasabeef.glide.transformations.BlurTransformation

@GlideExtension
object GlideAppExtension {
    // Size of mini thumb in pixels.
    private const val MINI_THUMB_SIZE = 100

    private val CROSS_FADE_FACTORY =
        DrawableCrossFadeFactory.Builder(150).setCrossFadeEnabled(false).build()

    @JvmField
    val DRAWABLE_CROSS_FADE = DrawableTransitionOptions.withCrossFade(CROSS_FADE_FACTORY)

    @JvmField
    val BITMAP_CROSS_FADE = BitmapTransitionOptions.withCrossFade(CROSS_FADE_FACTORY)

    private val blurMulti = RequestOptions.bitmapTransform(
        MultiTransformation(
            BlurTransformation(5, 1),
            //BrightnessFilterTransformation(-0.15f)
        )
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

    @JvmStatic
    @GlideOption
    fun blur(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.apply(blurMulti)
    }
}