package com.hym.zhankucompose.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import kotlinx.parcelize.Parcelize

/**
{
"avatar": "https://img.zcool.cn/community/031f1b95c60ae1fa801213f2663ce65.jpg",
"avatar1x": "https://img.zcool.cn/community/031f1b95c60ae1fa801213f2663ce65.jpg@80w_80h_1c_1e_1o_100sh.jpg",
"avatar2x": "https://img.zcool.cn/community/031f1b95c60ae1fa801213f2663ce65.jpg@160w_160h_1c_1e_1o_100sh.jpg",
"id": 199049,
"memberType": 0,
"pageUrl": "https://kimtao.zcool.com.cn",
"status": 1,
"username": "画画的淘米"
}
 */
@Parcelize
@Keep
@Immutable
data class CreatorObj(
    val avatar: String,
    val avatar1x: String,
    val avatar2x: String,
    val id: Int,
    val memberType: Int,
    val pageUrl: String,
    val status: Int,
    val username: String
) : Parcelable {
    val city: Int = 0
    val cityName: String = ""
    val contentCount: Int = 0
    val contentCountStr: String = ""
    val contentCountTips: String = ""
    val contentPageUrl: String = ""
    val fansCount: Int = 0
    val fansCountStr: String = ""
    val fansCountTips: String = ""
    val fansPageUrl: String = ""
    val guanzhuStatus: Int = 0

    val memberHonors: List<MemberHonor> = emptyList()

    val popularityCount: Int = 0
    val popularityCountStr: String = ""
    val popularityCountTips: String = ""
    val profession: Int = 0
    val professionName: String = ""
    val recommend: Int = 0
    val recommendTime: Long = 0L
    val signature: String = ""

    val contentCards: List<Content> = emptyList()

    companion object {
        val Demo = CreatorObj(
            avatar = "https://img.zcool.cn/community/031f1b95c60ae1fa801213f2663ce65.jpg",
            avatar1x = "https://img.zcool.cn/community/031f1b95c60ae1fa801213f2663ce65.jpg@80w_80h_1c_1e_1o_100sh.jpg",
            avatar2x = "https://img.zcool.cn/community/031f1b95c60ae1fa801213f2663ce65.jpg@160w_160h_1c_1e_1o_100sh.jpg",
            id = 199049,
            memberType = 0,
            pageUrl = "https://kimtao.zcool.com.cn",
            status = 1,
            username = "画画的淘米"
        )
    }
}