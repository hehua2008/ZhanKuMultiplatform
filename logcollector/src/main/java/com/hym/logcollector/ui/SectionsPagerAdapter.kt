package com.hym.logcollector.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hym.logcollector.R
import com.hym.logcollector.base.LogConfig

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to one of the sections/tabs/pages.
 */
internal class SectionsPagerAdapter(
    private val context: Context,
    fm: FragmentManager
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    companion object {
        private val TAB_TITLES = arrayOf(R.string.tab_logcat, R.string.tab_log_file)
    }

    private lateinit var mLogConfig: LogConfig

    fun setLogConfig(logConfig: LogConfig) {
        this.mLogConfig = logConfig
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) LogcatFragment.newInstance(mLogConfig)
        else LogPagingFragment.newInstance(mLogConfig)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return context.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int = TAB_TITLES.size

    override fun getItemPosition(fragment: Any): Int {
        if (fragment is LogcatFragment) {
            return if (fragment.logConfig == mLogConfig) POSITION_UNCHANGED else POSITION_NONE
        }
        if (fragment is LogPagingFragment) {
            return if (fragment.logConfig == mLogConfig) POSITION_UNCHANGED else POSITION_NONE
        }
        return POSITION_NONE
    }
}