package com.hym.zhankumultiplatform.ui.detail

import androidx.compose.runtime.Immutable
import com.fleeksoft.ksoup.Ksoup
import com.hym.zhankumultiplatform.model.ArticleDetails
import com.hym.zhankumultiplatform.model.ProductImage
import com.hym.zhankumultiplatform.model.ProductVideo

/**
 * @author hehua2008
 * @date 2022/8/12
 */
fun ArticleDetails.toDetailContent(): List<DetailContent<*>> {
    val doc = Ksoup.parse(articledata.memoHtml)
    val list = mutableListOf<DetailContent<*>>()
    val body = doc.selectFirst("body") ?: return list
    var stringId = 0
    val sb = StringBuilder()
    body.children().forEach {
        val img = it.selectFirst("img")
        if (img == null) {
            if (it.text().isNotBlank()) {
                sb.append(it.toString())
            }
        } else {
            if (sb.isNotEmpty()) {
                list.add(DetailText(data = sb.toString(), id = stringId++))
                sb.setLength(0)
            }
            val imageUrl = img.absUrl("src").let { url ->
                if (url.isNullOrBlank()) return@forEach
                url.substringBefore('?').substringBefore('@')
            }
            val articleImage = articleImageList.first { articleImage ->
                articleImage.img.contains(imageUrl, ignoreCase = true)
            }
            val productImage = ProductImage(
                0L,
                0,
                imageUrl,
                "",
                0,
                0,
                "",
                0,
                "",
                "",
                0,
                0L,
                0,
                0L,
                "",
                articleImage.middleImg,
                imageUrl,
                articleImage.smallImg,
                0
            )
            list.add(DetailImage(productImage))
        }
    }
    if (sb.isNotEmpty()) {
        list.add(DetailText(data = sb.toString(), id = stringId++))
        sb.setLength(0)
    }
    return list
}

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