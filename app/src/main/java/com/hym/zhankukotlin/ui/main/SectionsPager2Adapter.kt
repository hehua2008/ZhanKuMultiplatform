package com.hym.zhankukotlin.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hym.zhankukotlin.network.CategoryItem

class SectionsPager2Adapter(fragmentActivity: FragmentActivity) :
        FragmentStateAdapter(fragmentActivity) {
    private var mCategoryItems: List<CategoryItem> = emptyList()

    override fun createFragment(position: Int): Fragment {
        return PreviewItemFragment.newInstance(mCategoryItems[position])
    }

    override fun getItemCount(): Int {
        return mCategoryItems.size
    }

    fun setCategoryItems(categoryItems: List<CategoryItem>) {
        mCategoryItems = categoryItems
        notifyDataSetChanged()
    }
}