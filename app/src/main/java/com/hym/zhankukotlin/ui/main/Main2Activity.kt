package com.hym.zhankukotlin.ui.main

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.hym.zhankukotlin.BaseActivity
import com.hym.zhankukotlin.MyAppViewModel
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityMain2Binding
import com.hym.zhankukotlin.getAppViewModel
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.TabReselectedCallback
import com.hym.zhankukotlin.util.createTextColorStateListByColorAttr
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Main2Activity : BaseActivity(), TabConfigurationStrategy, OnTabSelectedListener {
    companion object {
        private const val TAG = "Main2Activity"
    }

    private lateinit var binding: ActivityMain2Binding
    private val sectionsPagerAdapter = SectionsPager2Adapter(this)
    private val mTopCates: MutableList<TopCate> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(false)
            }
        })
        super.onCreate(savedInstanceState)

        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
        binding.root.setLayerType(View.LAYER_TYPE_HARDWARE, Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0F) })
        })
        */
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val tabCount = binding.tabs.tabCount
                if (tabCount == 0) return
                for (i in 0 until tabCount) {
                    val tab = binding.tabs.getTabAt(i) ?: continue
                    val tabView = tab.customView as? AppCompatTextView ?: continue
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
        binding.tabs.addOnTabSelectedListener(this)

        val tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.viewPager, this)
        getAppViewModel<MyAppViewModel>().categoryItems.observe(this) { categoryItems ->
            mTopCates.clear()
            mTopCates.addAll(categoryItems)
            binding.viewPager.offscreenPageLimit = 1 + mTopCates.size
            sectionsPagerAdapter.setCategoryItems(mTopCates)
            binding.viewPager.currentItem = if (mTopCates.isEmpty()) 0 else 1
            tabLayoutMediator.detach()
            tabLayoutMediator.attach()
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        val fragment = sectionsPagerAdapter.getFragmentAt(binding.viewPager.currentItem)
        if (fragment is TabReselectedCallback) {
            fragment.onTabReselected()
        }
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        if (position == 0) {
            val tabView = AppCompatImageView(this)
            tabView.setImageResource(R.drawable.ic_keyword_search)
            tab.customView = tabView
            return
        }
        if (mTopCates.isEmpty()) return
        val tabView = AppCompatTextView(this)
        tabView.text = mTopCates[position - 1].name
        tabView.textSize = 16f
        tabView.gravity = Gravity.CENTER_HORIZONTAL
        tabView.setTextColor(createTextColorStateListByColorAttr())
        //tabView.isEnabled = false
        tab.customView = tabView
    }
}