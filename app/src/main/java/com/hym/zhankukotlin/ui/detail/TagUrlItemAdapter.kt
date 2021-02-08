package com.hym.zhankukotlin.ui.detail

import android.content.Intent
import com.google.android.material.button.MaterialButton
import com.hym.zhankukotlin.databinding.ButtonItemBinding
import com.hym.zhankukotlin.network.CategoryItem
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.NameValueAdapter
import com.hym.zhankukotlin.ui.main.PreviewItemFragment
import com.hym.zhankukotlin.ui.tag.TagActivity

class TagUrlItemAdapter : NameValueAdapter<String, String>() {
    override fun getOnCheckedChangeListener(
        holder: BindingViewHolder<ButtonItemBinding>, position: Int
    ): MaterialButton.OnCheckedChangeListener? {
        return null
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ButtonItemBinding>, position: Int
    ) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding
        binding.buttonView.isCheckable = false
        binding.buttonView.text = mNameValues[position].key
        binding.buttonView.setOnClickListener { v ->
            val tagUrl = mNameValues[position].value
            val context = v.context
            val intent = Intent(context, TagActivity::class.java)
            val categoryItem: CategoryItem = CategoryItem.getCategoryItem(tagUrl)
                ?: return@setOnClickListener
            intent.putExtra(PreviewItemFragment.CATEGORY_ITEM, categoryItem)
            context.startActivity(intent)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<ButtonItemBinding>) {
        super.onViewRecycled(holder)
        holder.binding.buttonView.setOnClickListener(null)
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