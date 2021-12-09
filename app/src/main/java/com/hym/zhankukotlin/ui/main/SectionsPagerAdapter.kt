package com.hym.zhankukotlin.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hym.zhankukotlin.model.TopCate

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var mTopCates: List<TopCate> = emptyList()

    override fun getItem(position: Int): Fragment {
        return PreviewItemFragment.newInstance(mTopCates[position])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mTopCates[position].name
    }

    override fun getCount(): Int {
        return mTopCates.size
    }

    fun setCategoryItems(categoryItems: List<TopCate>) {
        mTopCates = categoryItems
        notifyDataSetChanged()
    }

    override fun getItemPosition(fragment: Any): Int {
        if (fragment is PreviewItemFragment) {
            val topCateId = fragment.topCate?.id ?: return POSITION_NONE
            val index = mTopCates.indexOfFirst {
                it.id == topCateId
            }
            return if (index == -1) POSITION_NONE else index
        }
        return POSITION_NONE
    }
}