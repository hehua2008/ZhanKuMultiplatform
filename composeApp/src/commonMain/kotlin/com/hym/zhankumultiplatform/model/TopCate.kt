package com.hym.zhankumultiplatform.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

/**
{
"backgroundImage": "https://img.zcool.cn/community/0118e95f8eb014c97427536d4ccbed.jpg",
"commonOrderNo": 1,
"description": "以视觉为主的表现方式，包含包装，品牌，海报和字体等领域的优秀设计作品\r\n",
"descriptionEn": "以视觉为主的表现方式，包含包装，品牌，海报和字体等领域的优秀设计作品。",
"icon": "https://static.zcool.cn/git_z/z/images/svg/discover-cate-pingmian.svg",
"iconHover": "https://static.zcool.cn/git_z/z/images/svg/discover-cate-pingmian-hover.svg",
"id": 8,
"level": 1,
"name": "平面",
"nameEn": "Graphic Design",
"orderNo": 1,
"parent": 0,
"statusId": 1,
"subCateList": [...]
"type": 1
}
 */
@Serializable(TopCate.TopCateTypeAdapter::class)
@Immutable
data class TopCate(
    override val backgroundImage: String,
    override val commonOrderNo: Int,
    override val description: String,
    override val descriptionEn: String,
    override val icon: String,
    override val iconHover: String,
    override val id: Int,
    override val level: Int,
    override val name: String,
    override val nameEn: String,
    override val orderNo: Int,
    override val parent: Int,
    override val statusId: Int,
    override val type: Int,
    override val subCateList: List<SubCate>
) : Cate() {
    init {
        cache()
    }

    companion object TopCateTypeAdapter : CateTypeAdapter<TopCate>(TopCate::class) {
        override val cateCreator get() = CREATOR

        val CREATOR = object : CateCreator<TopCate>() {
            override fun create(
                backgroundImage: String,
                commonOrderNo: Int,
                description: String,
                descriptionEn: String,
                icon: String,
                iconHover: String,
                id: Int,
                level: Int,
                name: String,
                nameEn: String,
                orderNo: Int,
                parent: Int,
                statusId: Int,
                type: Int,
                subCateList: List<SubCate>
            ): TopCate {
                return TopCate(
                    backgroundImage,
                    commonOrderNo,
                    description,
                    descriptionEn,
                    icon,
                    iconHover,
                    id,
                    level,
                    name,
                    nameEn,
                    orderNo,
                    parent,
                    statusId,
                    type,
                    subCateList
                )
            }
        }

        val All = TopCate(
            backgroundImage = "",
            commonOrderNo = Int.MIN_VALUE,
            description = "全部",
            descriptionEn = "全部",
            icon = "",
            iconHover = "",
            id = Int.MIN_VALUE,
            level = Int.MIN_VALUE,
            name = "全部",
            nameEn = "全部",
            orderNo = Int.MIN_VALUE,
            parent = Int.MIN_VALUE,
            statusId = Int.MIN_VALUE,
            subCateList = emptyList(),
            type = Int.MIN_VALUE,
        )
    }
}