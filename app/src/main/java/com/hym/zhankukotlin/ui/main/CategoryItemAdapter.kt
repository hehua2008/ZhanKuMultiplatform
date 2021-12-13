package com.hym.zhankukotlin.ui.main

import com.google.android.material.button.MaterialButton
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.NameValueAdapter

class CategoryItemAdapter(
    private val topCate: TopCate?,
    private var subCate: SubCate?,
    private val mPageViewModel: PreviewPageViewModel
) : NameValueAdapter<String, SubCate?>() {
    init {
        topCate?.run {
            val titleUrlMap: MutableMap<String, SubCate?> = LinkedHashMap(1 + subCateList.size)
            titleUrlMap[name] = null
            for (subCate in subCateList) {
                titleUrlMap[subCate.name] = subCate
            }
            setNameValueMap(titleUrlMap)
        }
    }

    override fun getOnCheckedChangeListener(holder: ViewHolder, position: Int)
            : MaterialButton.OnCheckedChangeListener {
        return MaterialButton.OnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                mPageViewModel.setSubCate(mNameValues[position].value)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.button.text = mNameValues[position].key
        if (subCate == mNameValues[position].value) {
            subCate = null
            holder.button.isChecked = true
        }
    }
}