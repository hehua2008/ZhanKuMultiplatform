package com.hym.zhankucompose.model

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import androidx.core.text.HtmlCompat
import com.hym.zhankucompose.util.getDateTime
import com.hym.zhankucompose.util.getRelativeOrActualDateString

/**
{
"cate": 8,
"cateStr": "平面",
"commentCount": 14,
"commentCountStr": "14",
"contentCount": 0,
"contentCountStr": "0",
"cover": "https://img.zcool.cn/community/012c6061ae2b9e11013e8cd0590f32.jpg",
"cover1x": "https://img.zcool.cn/community/012c6061ae2b9e11013e8cd0590f32.jpg@260w_195h_1c_1e_1o_100sh.jpg",
"cover2x": "https://img.zcool.cn/community/012c6061ae2b9e11013e8cd0590f32.jpg@520w_390h_1c_1e_2o_100sh.jpg",
"createTime": 1638804411000,
"creator": 199049,
"creatorObj": {...},
"designTime": 1638748800000,
"eventId": 306,
"favoriteCount": 0,
"favoriteCountStr": "0",
"favoriteStatus": 0,
"id": 14199376,
"objectType": 3,
"objectTypeStr": "作品",
"pageUrl": "https://www.zcool.com.cn/work/ZNTY3OTc1MDQ=.html",
"personCount": 0,
"publishTime": 1638805956000,
"publishTimeDiffStr": "15小时前",
"recommend": 3,
"recommendCount": 68,
"recommendCountStr": "68",
"recommendTime": 1638862666000,
"status": 1,
"subCate": 779,
"subCateStr": "IP形象",
"timeTitleStr": "最近更新时间：2021-12-07 15:37:46&#10;首次审核通过：2021-12-06 23:52:36&#10;内容创建时间：2021-12-06 23:26:51",
"title": "#三体创意合伙人#智子工程计划",
"trackCode": "",
"type": 1,
"typeStr": "原创",
"viewCount": 301,
"viewCountStr": "301"
}
 */
@Keep
@Immutable
data class Content(
    val cate: Int,
    val cateStr: String,
    val commentCount: Int,
    val commentCountStr: String,
    val contentCount: Int,
    val contentCountStr: String,
    val cover: String,
    val cover1x: String,
    val cover2x: String,
    val createTime: Long,
    val creator: Int,
    val creatorObj: CreatorObj,
    val designTime: Long,
    val eventId: Int,
    val favoriteCount: Int,
    val favoriteCountStr: String,
    val favoriteStatus: Int,
    val id: Int,
    val objectType: Int,
    val objectTypeStr: String,
    val pageUrl: String,
    val personCount: Int,
    val publishTime: Long,
    val publishTimeDiffStr: String,
    val recommend: Int,
    val recommendCount: Int,
    val recommendCountStr: String,
    val recommendTime: Long,
    val status: Int,
    val subCate: Int,
    val subCateStr: String,
    val timeTitleStr: String,
    val title: String,
    val trackCode: String,
    val type: Int,
    val typeStr: String,
    val viewCount: Int,
    val viewCountStr: String
) {
    val contentId: String get() = pageUrl.split('/').last()

    val formatTitle get() = HtmlCompat.fromHtml(title, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()

    val updateTimeStr
        get() = (timeTitleStr.getDateTime() ?: publishTime).getRelativeOrActualDateString()

    companion object {
        val Demo = Content(
            cate = 8,
            cateStr = "平面",
            commentCount = 14,
            commentCountStr = "14",
            contentCount = 0,
            contentCountStr = "0",
            cover = "https://img.zcool.cn/community/012c6061ae2b9e11013e8cd0590f32.jpg",
            cover1x = "https://img.zcool.cn/community/012c6061ae2b9e11013e8cd0590f32.jpg@260w_195h_1c_1e_1o_100sh.jpg",
            cover2x = "https://img.zcool.cn/community/012c6061ae2b9e11013e8cd0590f32.jpg@520w_390h_1c_1e_2o_100sh.jpg",
            createTime = 1638804411000,
            creator = 199049,
            creatorObj = CreatorObj.Demo,
            designTime = 1638748800000,
            eventId = 306,
            favoriteCount = 0,
            favoriteCountStr = "0",
            favoriteStatus = 0,
            id = 14199376,
            objectType = 3,
            objectTypeStr = "作品",
            pageUrl = "https://www.zcool.com.cn/work/ZNTY3OTc1MDQ=.html",
            personCount = 0,
            publishTime = 1638805956000,
            publishTimeDiffStr = "15小时前",
            recommend = 3,
            recommendCount = 68,
            recommendCountStr = "68",
            recommendTime = 1638862666000,
            status = 1,
            subCate = 779,
            subCateStr = "IP形象",
            timeTitleStr = "最近更新时间：2021-12-07 15:37:46&#10;首次审核通过：2021-12-06 23:52:36&#10;内容创建时间：2021-12-06 23:26:51",
            title = "#三体创意合伙人#智子工程计划",
            trackCode = "",
            type = 1,
            typeStr = "原创",
            viewCount = 301,
            viewCountStr = "301"
        )
    }
}