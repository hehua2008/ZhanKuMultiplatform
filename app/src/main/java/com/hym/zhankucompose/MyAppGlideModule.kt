package com.hym.zhankucompose

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.hym.zhankucompose.hilt.NetworkModule
import dagger.hilt.android.EntryPointAccessors
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    companion object {
        private const val TAG = "MyAppGlideModule"
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.DEBUG)
        builder.setDefaultRequestOptions(
            RequestOptions()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
        )
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val appContext = context.applicationContext
        val accessor =
            EntryPointAccessors.fromApplication(appContext, NetworkModule.Accessor::class.java)
        val factory: OkHttpUrlLoader.Factory = OkHttpUrlLoader.Factory(accessor.okHttpClient())
        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }
}