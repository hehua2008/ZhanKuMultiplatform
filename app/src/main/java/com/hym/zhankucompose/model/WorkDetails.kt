package com.hym.zhankucompose.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
{
"totalpage": 1,
"product": {...},
"curPage": 1,
"productVideosIframe": [...],
"qrcode": "community/07721361aeb12e110153faced67fe1.jpg",
"sharewords": "成都约拍～<br>可惜的是橘子林都被套袋了[z大哭]"
}
 */
@Serializable
@Immutable
data class WorkDetails(
    val curPage: Int,
    val product: Product,
    val productVideosIframe: List<ProductVideosIframe>,
    val qrcode: String,
    val sharewords: String,
    val totalpage: Int
) {
    companion object {
        val Demo = WorkDetails(
            totalpage = 1,
            product = Product.Demo,
            curPage = 1,
            productVideosIframe = emptyList(),
            qrcode = "community/07721361aeb12e110153faced67fe1.jpg",
            sharewords = "成都约拍～<br>可惜的是橘子林都被套袋了[z大哭]"
        )
    }
}