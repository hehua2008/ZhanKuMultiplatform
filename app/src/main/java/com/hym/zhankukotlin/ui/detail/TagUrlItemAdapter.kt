package com.hym.zhankukotlin.ui.detail

import android.content.Intent
import com.google.android.material.button.MaterialButton
import com.hym.zhankukotlin.model.Cate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.NameValueAdapter
import com.hym.zhankukotlin.ui.main.PreviewItemFragment
import com.hym.zhankukotlin.ui.tag.TagActivity
import com.hym.zhankukotlin.util.ViewUtils.getActivity

class TagUrlItemAdapter : NameValueAdapter<String, Cate>() {
    override fun getOnCheckedChangeListener(holder: ViewHolder, position: Int)
            : MaterialButton.OnCheckedChangeListener? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.button.isCheckable = false
        holder.button.text = mNameValues[position].key
        holder.button.setOnClickListener { v ->
            val activity = v.getActivity() ?: return@setOnClickListener
            val tagCate = mNameValues[position].value
            val intent = Intent(activity, TagActivity::class.java)
            if (tagCate is TopCate) {
                intent.putExtra(PreviewItemFragment.TOP_CATE, tagCate)
            } else {
                intent.putExtra(
                    PreviewItemFragment.TOP_CATE,
                    Cate.getCategory<TopCate>(tagCate.parent)
                )
                intent.putExtra(PreviewItemFragment.SUB_CATE, tagCate)
            }
            activity.startActivity(intent)
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