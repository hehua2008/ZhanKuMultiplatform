package com.hym.zhankukotlin.ui.main

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityMain2Binding

class Main2Activity : AppCompatActivity(), TabConfigurationStrategy {
    private val mTitles: MutableList<String> = mutableListOf()

    private lateinit var mBinding: ActivityMain2Binding
    private lateinit var mSectionsPagerViewModel: SectionsPagerViewModel
    private lateinit var mMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        val sectionsPagerAdapter = SectionsPager2Adapter(this)
        mBinding.viewPager.adapter = sectionsPagerAdapter
        mBinding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val tabCount = mBinding.tabs.tabCount
                if (tabCount == 0) {
                    return
                }
                for (i in 0 until tabCount) {
                    val tab = mBinding.tabs.getTabAt(i) ?: continue
                    val tabView = tab.customView as TextView? ?: continue
                    if (tab.position == position) {
                        tabView.textSize = 16f
                        tabView.typeface = Typeface.DEFAULT_BOLD
                    } else {
                        tabView.textSize = 16f
                        tabView.typeface = Typeface.DEFAULT
                    }
                }
            }
        })

        mMediator = TabLayoutMediator(mBinding.tabs, mBinding.viewPager, this)
        mSectionsPagerViewModel = ViewModelProvider(this).get(SectionsPagerViewModel::class.java)
        mSectionsPagerViewModel.categoryItems.observe(this, Observer { categoryItems ->
            mBinding.viewPager.offscreenPageLimit = categoryItems.size
            sectionsPagerAdapter.setCategoryItems(categoryItems)
            mTitles.clear()
            for (categoryItem in categoryItems) {
                mTitles.add(categoryItem.title)
            }
            mMediator.detach()
            mMediator.attach()
        })

        mSectionsPagerViewModel.getCategoryItemsFromNetwork()
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        if (mTitles.isEmpty()) {
            return
        }
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = IntArray(0)
        val colors = intArrayOf(R.color.white, R.color.black)
        val colorStateList = ColorStateList(states, colors)
        val tabView = TextView(this)
        tabView.text = mTitles[position]
        tabView.textSize = 16f
        tabView.setTextColor(colorStateList)
        tab.customView = tabView
    }
}