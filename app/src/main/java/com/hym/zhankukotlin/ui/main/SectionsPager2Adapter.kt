package com.hym.zhankukotlin.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hym.zhankukotlin.model.TopCate

class SectionsPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private var mCategoryItems: List<TopCate> = emptyList()

    override fun createFragment(position: Int): Fragment {
        return PreviewItemFragment.newInstance(mCategoryItems[position])
    }

    override fun getItemCount(): Int {
        return mCategoryItems.size
    }

    override fun getItemId(position: Int): Long {
        return mCategoryItems[position].id.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return mCategoryItems.find { it.id.toLong() == itemId } != null
    }

    fun setCategoryItems(categoryItems: List<TopCate>) {
        mCategoryItems = categoryItems
        notifyDataSetChanged()
    }
}