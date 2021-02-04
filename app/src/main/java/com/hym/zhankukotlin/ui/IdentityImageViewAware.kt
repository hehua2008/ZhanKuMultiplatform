package com.hym.zhankukotlin.ui

import android.widget.ImageView
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware

class IdentityImageViewAware : ImageViewAware {
    constructor(imageView: ImageView) : super(imageView) {}

    constructor(imageView: ImageView, checkActualViewSize: Boolean)
            : super(imageView, checkActualViewSize) {
    }

    override fun getId(): Int {
        return hashCode()
    }
}