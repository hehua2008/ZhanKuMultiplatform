package com.hym.zhankucompose.ui.detail

import android.content.Intent
import com.google.android.material.button.MaterialButton
import com.hym.zhankucompose.model.Cate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.NameValueAdapter
import com.hym.zhankucompose.ui.main.PreviewItemFragment
import com.hym.zhankucompose.ui.tag.TagActivity

class TagUrlItemAdapter : NameValueAdapter<String, Cate>() {
    override fun getOnCheckedChangeListener(holder: ViewHolder, position: Int)
            : MaterialButton.OnCheckedChangeListener? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.button.isCheckable = false
        holder.button.text = mNameValues[position].key
        holder.button.setOnClickListener { v ->
            val context = v.context
            val tagCate = mNameValues[position].value
            val intent = Intent(context, TagActivity::class.java)
            if (tagCate is TopCate) {
                intent.putExtra(PreviewItemFragment.TOP_CATE, tagCate)
            } else {
                intent.putExtra(
                    PreviewItemFragment.TOP_CATE,
                    Cate.getCategory<TopCate>(tagCate.parent)
                )
                intent.putExtra(PreviewItemFragment.SUB_CATE, tagCate)
            }
            context.startActivity(intent)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.button.setOnClickListener(null)
    }

    fun setTagItems(tagItems: List<Cate>) {
        val tagCateMap: MutableMap<String, Cate> = LinkedHashMap(tagItems.size)
        for (tagItem in tagItems) {
            tagCateMap[tagItem.name] = tagItem
        }
        setNameValueMap(tagCateMap)
    }

    fun setTagCateMap(tagCateMap: Map<String, Cate>) {
        setNameValueMap(tagCateMap)
    }
}