package com.hym.zhankucompose.model

import kotlinx.serialization.Serializable

/**
{
"articleImageList": [...],
"qrcode": "community/07b45162f48b230002dd1c10c5f65e.jpg",
"sharewords": "///一、长线运营活动的价值百度APP作为移动生态的搜索信息服务产品，用户会在什么场景想到百度APP并使用它呢？十几年前，精力有限睡眠有限的打工人不惜牺牲睡眠时间，也要设置凌晨3点的闹钟；上网时间被严格控制的学生党也要注册五六个QQ小号，目的很明确：到点“收菜”，蹲点“偷菜”。十几年后每天早上睁眼先去蚂蚁森林收收自己的“能量”，再顺路去蚂蚁庄园喂喂小鸡，安排很多手机号码来种水果……各家产品通过带有游戏氛围的运营活动把产品的服务和功能以趣味化的方式传递给用户，让用户“薅羊毛”拿福利的同时也给产品带来增长价值。",
"articledata": {..}
}
 */
@Serializable
data class ArticleDetails(
    val articleImageList: List<ArticleImage>,
    val articledata: ArticleData,
    val qrcode: String,
    val sharewords: String
)