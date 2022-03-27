package com.hym.photoviewer

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomViewTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference

/**
 * @author hehua2008
 * @date 2022/3/8
 */
class PhotoViewTarget(photoView: PhotoView) : CustomViewTarget<PhotoView, Bitmap>(photoView) {
    private var resourceRef: WeakReference<Bitmap>? = null
    val resource: Bitmap? get() = resourceRef?.get()

    override fun onLoadFailed(errorDrawable: Drawable?) {
        errorDrawable?.let { view.bindDrawable(it) }
    }

    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
        resourceRef = WeakReference(resource)
        view.bindPhoto(resource)
    }

    override fun onResourceCleared(placeholder: Drawable?) {
        val isReload = Thread.currentThread().stackTrace.any {
            it.methodName == "into" && it.className == "com.bumptech.glide.RequestBuilder"
        }
        if (isReload) return
        view.bindDrawable(placeholder)
    }

    override fun onResourceLoading(placeholder: Drawable?) {
        placeholder?.let { view.bindDrawable(it) }
    }
}