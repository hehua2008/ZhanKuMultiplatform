package com.hym.zhankucompose.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
{
"createTime": 1638871464000,
"creator": 12932945,
"description": "",
"height": 0,
"id": 566618,
"name": "发热围巾-30秒预告片-导演版.mp4",
"orderNo": 0,
"productId": 14203765,
"status": 1,
"type": 4,
"updateTime": 1638871464000,
"url": "https://video.zcool.cn/a3d9f2c07855493eb5fc1f7de86a12af/de5d5c04755c4700b9ec704fb1ced3b6-5287d2089db37e62345123a1be272f8b.mp4?auth_key=1638945414-4e02204bad92466c919d9d356bdb447e-0-456e25c54cd6ee51b858ab3d51c196d8",
"videoId": "a3d9f2c07855493eb5fc1f7de86a12af",
"width": 0
}
 */
@Serializable
@Immutable
data class ProductVideosIframe(
    val createTime: Long,
    val creator: Int,
    val description: String,
    val height: Int,
    val id: Int,
    val name: String,
    val orderNo: Int,
    val productId: Int,
    val status: Int,
    val type: Int,
    val updateTime: Long,
    val url: String,
    val videoId: String,
    val width: Int
)