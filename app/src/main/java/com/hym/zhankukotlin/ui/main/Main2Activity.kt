package com.hym.zhankukotlin.ui.main

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityMain2Binding
import com.hym.zhankukotlin.model.TopCate

class Main2Activity : AppCompatActivity(), TabConfigurationStrategy {
    companion object {
        private const val TOP_CATES = "TOP_CATES"
    }

    private val mTopCates: ArrayList<TopCate> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.getParcelableArrayList<TopCate>(TOP_CATES)?.let { mTopCates.addAll(it) }

        val binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionsPagerAdapter = SectionsPager2Adapter(this)
        sectionsPagerAdapter.setCategoryItems(mTopCates)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val tabCount = binding.tabs.tabCount
                if (tabCount == 0) return
                for (i in 0 until tabCount) {
                    val tab = binding.tabs.getTabAt(i) ?: continue
                    val tabView = tab.customView as? TextView ?: continue
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

        val tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.viewPager, this)
        val sectionsPagerViewModel = ViewModelProvider(this).get(SectionsPagerViewModel::class.java)
        sectionsPagerViewModel.categoryItems.observe(this) { categoryItems ->
            mTopCates.clear()
            mTopCates.addAll(categoryItems)
            binding.viewPager.offscreenPageLimit = mTopCates.size
            sectionsPagerAdapter.setCategoryItems(mTopCates)
            tabLayoutMediator.detach()
            tabLayoutMediator.attach()
        }

        sectionsPagerViewModel.getCategoryItemsFromNetwork()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(TOP_CATES, mTopCates)
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        if (mTopCates.isEmpty()) return
        val states = arrayOfNulls<IntArray>(2)
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = IntArray(0)
        val colors = intArrayOf(R.color.white, R.color.black)
        val colorStateList = ColorStateList(states, colors)
        val tabView = TextView(this)
        tabView.text = mTopCates[position].name
        tabView.textSize = 16f
        tabView.setTextColor(colorStateList)
        tab.customView = tabView
    }
}