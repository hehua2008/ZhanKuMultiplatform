package com.hym.zhankucompose.model

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable

/**
{
"backgroundImage": "https://img.zcool.cn/community/013e07603ca975c97427790bfdd0c4.png",
"commonOrderNo": 1,
"description": "商品容器的标签、外形或结构设计",
"descriptionEn": "商品容器的标签、外形或结构设计",
"icon": "",
"iconHover": "",
"id": 9,
"level": 2,
"name": "包装",
"nameEn": "Packaging",
"orderNo": 1,
"parent": 8,
"statusId": 1,
"type": 1
}
 */
@Keep
@Immutable
data class SubCate(
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
    override val type: Int
) : Cate() {
    @Transient
    override val subCateList: List<SubCate> = emptyList()

    init {
        cache()
    }

    companion object {
        @JvmField
        val CREATOR = object : CateCreator<SubCate>() {
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
                subCateList: List<SubCate>,
                type: Int
            ): SubCate {
                return SubCate(
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
                    type
                )
            }
        }

        val SubCateTypeAdapter = object : CateTypeAdapter<SubCate>() {
            override val cateCreator get() = CREATOR
        }

        val Demo = SubCate(
            backgroundImage = "https://img.zcool.cn/community/013e07603ca975c97427790bfdd0c4.png",
            commonOrderNo = 1,
            description = "商品容器的标签、外形或结构设计",
            descriptionEn = "商品容器的标签、外形或结构设计",
            icon = "",
            iconHover = "",
            id = 9,
            level = 2,
            name = "包装",
            nameEn = "Packaging",
            orderNo = 1,
            parent = 8,
            statusId = 1,
            type = 1
        )
    }
}