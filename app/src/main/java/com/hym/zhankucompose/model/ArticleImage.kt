package com.hym.zhankucompose.model

import androidx.annotation.Keep

/**
{
"bigImg": "https://img.zcool.cn/community/02jgwoesokgnu4gwk4642n3536.jpg?x-oss-process=image/auto-orient,1/resize,m_lfit,w_1200,limit_1/sharpen,100/quality,q_100",
"img": "https://img.zcool.cn/community/02jgwoesokgnu4gwk4642n3536.jpg",
"smallImg": "https://img.zcool.cn/community/02jgwoesokgnu4gwk4642n3536.jpg?x-oss-process=image/auto-orient,1/resize,m_lfit,w_500,limit_1/sharpen,100/quality,q_100",
"middleImg": "https://img.zcool.cn/community/02jgwoesokgnu4gwk4642n3536.jpg?x-oss-process=image/auto-orient,1/resize,m_lfit,w_800,limit_1/sharpen,100/quality,q_100"
}
 */
@Keep
data class ArticleImage(
    val bigImg: String,
    val img: String,
    val middleImg: String,
    val smallImg: String
)