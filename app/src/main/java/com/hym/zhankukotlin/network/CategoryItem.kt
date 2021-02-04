package com.hym.zhankukotlin.network

import android.os.Parcel
import android.os.Parcelable

class CategoryItem private constructor(
    val url: String,
    val title: String
) : Parcelable {
    private val mSubItems: MutableList<CategoryItem> = mutableListOf()
    private val mSub2Items: MutableList<CategoryItem> = mutableListOf()

    fun hasSubItems(): Boolean {
        return mSubItems.isNotEmpty()
    }

    fun hasSub2Items(): Boolean {
        return mSub2Items.isNotEmpty()
    }

    val subItems: List<CategoryItem>
        get() = mSubItems

    val sub2Items: List<CategoryItem>
        get() = mSub2Items

    fun addSubItem(subItem: CategoryItem) {
        mSubItems.add(subItem)
    }

    fun addSubItem(subUrl: String, subTitle: String) {
        addSubItem(CategoryItem(subUrl, subTitle))
    }

    fun addSub2Item(sub2Item: CategoryItem) {
        mSub2Items.add(sub2Item)
    }

    fun addSub2Item(subUrl: String, subTitle: String) {
        addSub2Item(CategoryItem(subUrl, subTitle))
    }

    override fun toString(): String {
        val sb = StringBuilder(title).append(" : ").append(url)
        if (hasSubItems()) {
            sb.append('\n').append(mSubItems)
        }
        if (hasSub2Items()) {
            sb.append('\n').append(mSub2Items)
        }
        sb.append('\n')
        return sb.toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
    }

    companion object CREATOR : Parcelable.Creator<CategoryItem?> {
        private val URL_ITEM_MAP: MutableMap<String, CategoryItem> = HashMap()
        val ERROR = CategoryItem("ERROR", "ERROR")

        fun getCategoryItem(url: String?): CategoryItem? {
            return URL_ITEM_MAP[url]
        }

        fun createCategoryItem(url: String, title: String): CategoryItem {
            val cat = URL_ITEM_MAP[url]
            return cat ?: CategoryItem(url, title)
        }

        override fun createFromParcel(parcel: Parcel): CategoryItem? {
            val url = parcel.readString()
            return URL_ITEM_MAP[url]
        }

        override fun newArray(size: Int): Array<CategoryItem?> {
            return arrayOfNulls(size)
        }
    }

    init {
        URL_ITEM_MAP[url] = this
    }
}