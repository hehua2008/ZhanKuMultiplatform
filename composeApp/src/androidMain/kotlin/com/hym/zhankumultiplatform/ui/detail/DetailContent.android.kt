package com.hym.zhankumultiplatform.ui.detail

import com.hym.zhankumultiplatform.model.ArticleDetails
import com.hym.zhankumultiplatform.model.ProductImage
import org.jsoup.Jsoup

/**
 * @author hehua2008
 * @date 2022/8/12
 */
actual fun ArticleDetails.toDetailContent(): List<DetailContent<*>> {
    val doc = Jsoup.parse(articledata.memoHtml)
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
