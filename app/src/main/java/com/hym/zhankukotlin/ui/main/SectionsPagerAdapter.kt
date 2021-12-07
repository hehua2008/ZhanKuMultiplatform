package com.hym.zhankukotlin.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hym.zhankukotlin.network.CategoryItem

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var mCategoryItems: List<CategoryItem> = emptyList()

    override fun getItem(position: Int): Fragment {
        return PreviewItemFragment.newInstance(mCategoryItems[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mCategoryItems[position].title
    }

    override fun getCount(): Int {
        return mCategoryItems.size
    }

    fun setCategoryItems(categoryItems: List<CategoryItem>) {
        mCategoryItems = categoryItems
        notifyDataSetChanged()
    }
}