package com.hym.zhankucompose.ui.search

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hym.zhankucompose.model.ContentType

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SearchPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var currentFragment: Fragment? = null
        private set

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> SearchContentFragment.newInstance(ContentType.WORK)
            1 -> SearchContentFragment.newInstance(ContentType.ARTICLE)
            else -> TODO()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return when (position) {
            0 -> ContentType.WORK.text
            1 -> ContentType.ARTICLE.text
            else -> TODO()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, fragment: Any) {
        super.setPrimaryItem(container, position, fragment)
        currentFragment = fragment as Fragment
    }
}