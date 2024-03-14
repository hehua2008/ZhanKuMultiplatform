package com.hym.zhankucompose.model

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Immutable

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
@Immutable
abstract class Cate : Parcelable {
    abstract val backgroundImage: String

    abstract val commonOrderNo: Int

    abstract val description: String

    abstract val descriptionEn: String

    abstract val icon: String

    abstract val iconHover: String

    abstract val id: Int

    abstract val level: Int

    abstract val name: String

    abstract val nameEn: String

    abstract val orderNo: Int

    abstract val parent: Int

    abstract val statusId: Int

    abstract val subCateList: List<SubCate>

    abstract val type: Int

    final override fun hashCode(): Int = id

    final override fun describeContents(): Int = 0

    final override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
    }

    companion object {
        private val CATE_MAP: MutableMap<Int, Cate> = mutableMapOf()

        fun <T : Cate> T.cache() {
            val old = CATE_MAP[id]
            if (old == null || (old.subCateList.isEmpty() && subCateList.isNotEmpty())) {
                CATE_MAP[id] = this
            }
        }

        fun <T : Cate> getCategory(id: Int): T? {
            return CATE_MAP[id] as? T
        }
    }

    abstract class CateCreator<T : Cate> : Parcelable.Creator<T> {
        abstract fun create(
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
        ): T

        override fun createFromParcel(source: Parcel): T {
            val id = source.readInt()
            return getCategory(id)!!
        }

        override fun newArray(size: Int): Array<T?> {
            return Array<Any?>(size) { null } as Array<T?>
        }
    }
}