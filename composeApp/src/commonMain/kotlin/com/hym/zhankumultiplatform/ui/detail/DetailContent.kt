package com.hym.zhankumultiplatform.ui.detail

import androidx.compose.runtime.Immutable
import com.hym.zhankumultiplatform.model.ArticleDetails
import com.hym.zhankumultiplatform.model.ProductImage
import com.hym.zhankumultiplatform.model.ProductVideo

/**
 * @author hehua2008
 * @date 2022/8/12
 */
expect fun ArticleDetails.toDetailContent(): List<DetailContent<*>>

@Immutable
sealed class DetailContent<T>(
    open val data: T
) {
    abstract val type: Int

    companion object {
        const val CONTENT_IMAGE = 1
        const val CONTENT_VIDEO = 2
        const val CONTENT_TEXT = 3
    }

    open val id: Int get() = hashCode()

    private var hash: Int? = null

    override fun hashCode(): Int {
        return hash ?: data.hashCode().also { hash = it }
    }

    open fun shallowEquals(other: DetailContent<*>?): Boolean {
        return false
    }
}

@Immutable
data class DetailImage(
    override val data: ProductImage
) : DetailContent<ProductImage>(data) {
    override val type: Int = CONTENT_IMAGE

    override fun shallowEquals(other: DetailContent<*>?): Boolean {
        if (this === other) return true
        if (other !is DetailImage) return false
        return data.oriUrl == other.data.oriUrl
    }
}

@Immutable
data class DetailVideo(
    override val data: ProductVideo
) : DetailContent<ProductVideo>(data) {
    override val type: Int = CONTENT_VIDEO

    override fun shallowEquals(other: DetailContent<*>?): Boolean {
        if (this === other) return true
        if (other !is DetailVideo) return false
        return data.url == other.data.url
    }
}

@Immutable
data class DetailText(
    override val data: String,
    override val id: Int
) : DetailContent<String>(data) {
    override val type: Int = CONTENT_TEXT

    override fun shallowEquals(other: DetailContent<*>?): Boolean {
        if (this === other) return true
        if (other !is DetailText) return false
        return data == other.data
    }
}