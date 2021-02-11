package com.hym.zhankukotlin.ui.detail

import android.content.Intent
import com.google.android.material.button.MaterialButton
import com.hym.zhankukotlin.network.CategoryItem
import com.hym.zhankukotlin.ui.NameValueAdapter
import com.hym.zhankukotlin.ui.main.PreviewItemFragment
import com.hym.zhankukotlin.ui.tag.TagActivity
import com.hym.zhankukotlin.util.ViewUtils.getActivityContext

class TagUrlItemAdapter : NameValueAdapter<String, String>() {
    override fun getOnCheckedChangeListener(holder: ViewHolder, position: Int)
            : MaterialButton.OnCheckedChangeListener? {
        return null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.button.isCheckable = false
        holder.button.text = mNameValues[position].key
        holder.button.setOnClickListener { v ->
            val context = v.getActivityContext() ?: return@setOnClickListener
            val tagUrl = mNameValues[position].value
            val intent = Intent(context, TagActivity::class.java)
            val categoryItem: CategoryItem = CategoryItem.getCategoryItem(tagUrl)
                    ?: return@setOnClickListener
            intent.putExtra(PreviewItemFragment.CATEGORY_ITEM, categoryItem)
            context.startActivity(intent)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.button.setOnClickListener(null)
    }

    fun setTagItems(tagItems: List<CategoryItem>) {
        val tagUrlMap: MutableMap<String, String> = LinkedHashMap(tagItems.size)
        for (tagItem in tagItems) {
            tagUrlMap[tagItem.title] = tagItem.url
        }
        setNameValueMap(tagUrlMap)
    }

    fun setTagUrlMap(tagUrlMap: Map<String, String>) {
        setNameValueMap(tagUrlMap)
    }
}