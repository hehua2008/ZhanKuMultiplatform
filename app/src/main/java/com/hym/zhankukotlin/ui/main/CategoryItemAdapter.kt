package com.hym.zhankukotlin.ui.main

import com.google.android.material.button.MaterialButton
import com.hym.zhankukotlin.databinding.ButtonItemBinding
import com.hym.zhankukotlin.network.CategoryItem
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.NameValueAdapter

class CategoryItemAdapter(private val mPageViewModel: PageViewModel) :
        NameValueAdapter<String, String>() {
    override fun getOnCheckedChangeListener(
            holder: BindingViewHolder<ButtonItemBinding>, position: Int
    ): MaterialButton.OnCheckedChangeListener {
        return MaterialButton.OnCheckedChangeListener { button, isChecked ->
            if (isChecked) {
                mPageViewModel.setSubcat(mNameValues[position].value)
            }
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ButtonItemBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding
        binding.buttonView.text = mNameValues[position].key
    }

    fun setTitleSubcatMap(parentCategoryItem: CategoryItem) {
        val subItems = parentCategoryItem.subItems
        val sub2Items = parentCategoryItem.sub2Items
        val size = 1 + subItems.size + sub2Items.size
        if (size == 1) {
            return
        }
        val titleUrlMap: MutableMap<String, String> = LinkedHashMap(size)
        titleUrlMap[parentCategoryItem.title] = getSubcat(parentCategoryItem.url)
        for (subItem in subItems) {
            titleUrlMap[subItem.title] = getSubcat(subItem.url)
        }
        for (sub2Item in sub2Items) {
            titleUrlMap[sub2Item.title] = getSubcat(sub2Item.url)
        }
        setTitleSubcatMap(titleUrlMap)
    }

    fun setTitleSubcatMap(titleSubcatMap: Map<String, String>) {
        setNameValueMap(titleSubcatMap)
    }

    private companion object {
        fun getSubcat(url: String): String {
            val first = url.indexOf('!')
            if (first < 0 || first == url.length - 1) {
                return ""
            }
            val second = url.indexOf('!', first + 1)
            if (second < 0 || second == url.length - 1) {
                return ""
            }
            val third = url.indexOf('!', second + 1)
            return if (third < 0) {
                ""
            } else url.substring(first + 1, third + 1)
        }
    }
}