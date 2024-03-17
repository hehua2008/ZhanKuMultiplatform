package com.hym.zhankucompose.ui.detail

import com.hym.zhankucompose.model.ArticleDetails
import com.hym.zhankucompose.model.ProductImage
import com.hym.zhankucompose.model.ProductVideo
import org.jsoup.Jsoup

/**
 * @author hehua2008
 * @date 2022/8/12
 */
sealed class DetailContent<T>(val type: Int, val data: T) {
    companion object {
        const val CONTENT_IMAGE = 1
        const val CONTENT_VIDEO = 2
        const val CONTENT_TEXT = 3

        fun articleDetailsToDetailContent(articleDetails: ArticleDetails): List<DetailContent<*>> {
            val doc = Jsoup.parse(articleDetails.articledata.memoHtml)
            val list = mutableListOf<DetailContent<*>>()
            val body = doc.selectFirst("body") ?: return list
            val sb = StringBuilder()
            body.children().forEach {
                val img = it.selectFirst("img")
                if (img == null) {
                    if (it.text().isNotBlank()) {
                        sb.append(it.toString())
                    }
                } else {
                    if (sb.isNotEmpty()) {
                        list.add(DetailText(sb.toString()))
                        sb.setLength(0)
                    }
                    val imageUrl = img.absUrl("src").let { url ->
                        if (url.isNullOrBlank()) return@forEach
                        url.substringBefore('?').substringBefore('@')
                    }
                    val articleImage = articleDetails.articleImageList.first { articleImage ->
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
                list.add(DetailText(sb.toString()))
                sb.setLength(0)
            }
            return list
        }
    }

    override fun hashCode(): Int {
        return data.hashCode()
    }

    open fun shallowEquals(other: DetailContent<*>?): Boolean {
        return false
    }
}

class DetailImage(image: ProductImage) : DetailContent<ProductImage>(CONTENT_IMAGE, image) {
    override fun shallowEquals(other: DetailContent<*>?): Boolean {
        if (this === other) return true
        if (other !is DetailImage) return false
        return data.oriUrl == other.data.oriUrl
    }
}

class DetailVideo(video: ProductVideo) : DetailContent<ProductVideo>(CONTENT_VIDEO, video) {
    override fun shallowEquals(other: DetailContent<*>?): Boolean {
        if (this === other) return true
        if (other !is DetailVideo) return false
        return data.url == other.data.url
    }
}

class DetailText(article: String) : DetailContent<String>(CONTENT_TEXT, article) {
    override fun shallowEquals(other: DetailContent<*>?): Boolean {
        if (this === other) return true
        if (other !is DetailText) return false
        return data == other.data
    }
}