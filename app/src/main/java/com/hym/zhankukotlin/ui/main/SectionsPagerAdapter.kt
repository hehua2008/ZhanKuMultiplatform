package com.hym.zhankukotlin.ui.main

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.search.SearchFragment

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private var mTopCates: List<TopCate> = emptyList()

    var currentFragment: Fragment? = null
        private set

    override fun getItem(position: Int): Fragment {
        return if (position == 0) SearchFragment()
        else PreviewItemFragment.newInstance(mTopCates[position - 1])
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (position == 0) MyApplication.INSTANCE.getString(R.string.title_search)
        else mTopCates[position - 1].name
    }

    override fun getCount(): Int {
        return 1 + mTopCates.size
    }

    fun setCategoryItems(categoryItems: List<TopCate>) {
        mTopCates = categoryItems
        notifyDataSetChanged()
    }

    override fun getItemPosition(fragment: Any): Int {
        if (fragment is SearchFragment) return POSITION_UNCHANGED
        if (fragment is PreviewItemFragment) {
            val topCateId = fragment.topCate?.id ?: return POSITION_NONE
            val index = mTopCates.indexOfFirst {
                it.id == topCateId
            }
            return if (index == -1) POSITION_NONE else 1 + index
        }
        return POSITION_NONE
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: Any) {
        super.setPrimaryItem(container, position, fragment)
        currentFragment = fragment as Fragment
    }
}