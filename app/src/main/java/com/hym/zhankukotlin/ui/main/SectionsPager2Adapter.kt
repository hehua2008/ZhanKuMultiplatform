package com.hym.zhankukotlin.ui.main

import androidx.collection.LongSparseArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.search.SearchFragment

class SectionsPager2Adapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    private var mCategoryItems: List<TopCate> = emptyList()

    private val mFragments: LongSparseArray<Fragment> = run {
        val field = FragmentStateAdapter::class.java.getDeclaredField("mFragments")
        field.isAccessible = true
        field.get(this) as LongSparseArray<Fragment>
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) SearchFragment()
        else PreviewItemFragment.newInstance(mCategoryItems[position - 1])
    }

    override fun getItemCount(): Int {
        return 1 + mCategoryItems.size
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0) Long.MIN_VALUE
        else mCategoryItems[position - 1].id.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return if (itemId == Long.MIN_VALUE) true
        else mCategoryItems.find { it.id.toLong() == itemId } != null
    }

    fun setCategoryItems(categoryItems: List<TopCate>) {
        mCategoryItems = categoryItems
        notifyDataSetChanged()
    }

    fun getFragmentAt(position: Int): Fragment? {
        if (position < 0 || position >= itemCount) return null
        val itemId = getItemId(position)
        return mFragments[itemId]
    }
}