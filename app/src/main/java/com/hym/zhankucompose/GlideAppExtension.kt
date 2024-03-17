package com.hym.zhankucompose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.placeholder
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.BaseRequestOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.hym.zhankucompose.hilt.NetworkModule
import dagger.hilt.android.EntryPointAccessors
import jp.wasabeef.glide.transformations.BlurTransformation

@GlideExtension
object GlideAppExtension {
    // Size of mini thumb in pixels.
    const val MINI_THUMB_SIZE = 100

    val CROSS_FADE_FACTORY =
        DrawableCrossFadeFactory.Builder(150).setCrossFadeEnabled(false).build()

    @JvmField
    val DRAWABLE_CROSS_FADE = DrawableTransitionOptions.withCrossFade(CROSS_FADE_FACTORY)

    @JvmField
    val BITMAP_CROSS_FADE = BitmapTransitionOptions.withCrossFade(CROSS_FADE_FACTORY)

    val blurMulti = RequestOptions.bitmapTransform(
        MultiTransformation(
            BlurTransformation(5, 1),
            //BrightnessFilterTransformation(-0.15f)
        )
    )

    @OptIn(ExperimentalGlideComposeApi::class)
    val TransparentPlaceholder = placeholder(ColorPainter(Color.Transparent))

    @Deprecated("Deprecate Glide's Extensions, GlideApp, GlideRequest and GlideRequests.")
    @JvmStatic
    @GlideOption
    fun miniThumb(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.fitCenter().override(MINI_THUMB_SIZE)
    }

    @Deprecated("Deprecate Glide's Extensions, GlideApp, GlideRequest and GlideRequests.")
    @JvmStatic
    @GlideOption
    fun transparentPlaceHolder(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        val app = MyApplication.INSTANCE
        val accessor = EntryPointAccessors.fromApplication(app, NetworkModule.Accessor::class.java)
        return options.placeholder(accessor.transparentDrawable())
    }

    @Deprecated("Deprecate Glide's Extensions, GlideApp, GlideRequest and GlideRequests.")
    @JvmStatic
    @GlideOption
    fun originalSize(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.override(Target.SIZE_ORIGINAL)
    }

    @Deprecated("Deprecate Glide's Extensions, GlideApp, GlideRequest and GlideRequests.")
    @JvmStatic
    @GlideOption
    fun blur(options: BaseRequestOptions<*>): BaseRequestOptions<*> {
        return options.apply(blurMulti)
    }
}