package com.hym.zhankukotlin.ui

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import com.hym.zhankukotlin.R
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener

class ImageViewLoadingListener private constructor(imageView: ImageView) :
    SimpleImageLoadingListener() {
    val imageAware: IdentityImageViewAware = IdentityImageViewAware(imageView)

    override fun onLoadingComplete(imageUri: String, view: View, loadedImage: Bitmap) {
        val imageView = imageAware.wrappedView ?: return
        val imageViewUri = imageView.getTag(R.id.image_view_uri_key)
        if (imageViewUri is String && imageUri != imageViewUri) {
            return
        }
        val loaded = imageView.getTag(R.id.image_view_loaded_key)
        if (loaded == java.lang.Boolean.TRUE) {
            return
        }
        imageView.setImageBitmap(loadedImage)
        imageView.setTag(R.id.image_view_loaded_key, true)
    }

    companion object {
        @JvmStatic
        fun getListener(imageView: ImageView): ImageViewLoadingListener? {
            return imageView.getTag(R.id.image_view_listener_key) as ImageViewLoadingListener
        }

        @JvmStatic
        fun createListener(imageView: ImageView, imageUri: String): ImageViewLoadingListener {
            var listener = imageView.getTag(R.id.image_view_listener_key)
            if (listener !is ImageViewLoadingListener) {
                listener = ImageViewLoadingListener(imageView)
            }
            imageView.setTag(R.id.image_view_uri_key, imageUri)
            return listener
        }

        @JvmStatic
        fun shouldReLoadImage(imageView: ImageView, imageUri: String): Boolean {
            val oldUri = imageView.getTag(R.id.image_view_uri_key)
            if (oldUri is String && imageUri != oldUri) {
                return true
            }
            val loaded = imageView.getTag(R.id.image_view_loaded_key)
            return loaded != java.lang.Boolean.TRUE
        }

        @JvmStatic
        fun resetImageViewTags(imageView: ImageView) {
            imageView.setTag(R.id.image_view_uri_key, null)
            imageView.setTag(R.id.image_view_loaded_key, null)
        }
    }

    init {
        imageView.setTag(R.id.image_view_listener_key, this)
    }
}