package com.hym.zhankukotlin.ui.main

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.hym.zhankukotlin.MyAppViewModel
import com.hym.zhankukotlin.databinding.ActivityMain2Binding
import com.hym.zhankukotlin.getAppViewModel
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.util.createTextColorStateListByColorAttr

class Main2Activity : AppCompatActivity(), TabConfigurationStrategy {
    companion object {
        private const val TAG = "Main2Activity"
    }

    private val mTopCates: MutableList<TopCate> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(false)
            }
        })
        super.onCreate(savedInstanceState)

        val binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        /*
        binding.root.setLayerType(View.LAYER_TYPE_HARDWARE, Paint().apply {
            colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0F) })
        })
        */
        val sectionsPagerAdapter = SectionsPager2Adapter(this)
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

        val tabLayoutMediator = TabLayoutMediator(binding.tabs, binding.viewPager, this)
        getAppViewModel<MyAppViewModel>().categoryItems.observe(this) { categoryItems ->
            mTopCates.clear()
            mTopCates.addAll(categoryItems)
            binding.viewPager.offscreenPageLimit = mTopCates.size
            sectionsPagerAdapter.setCategoryItems(mTopCates)
            tabLayoutMediator.detach()
            tabLayoutMediator.attach()
        }
    }

    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
        if (mTopCates.isEmpty()) return
        val tabView = AppCompatTextView(this)
        tabView.text = mTopCates[position].name
        tabView.textSize = 16f
        tabView.gravity = Gravity.CENTER_HORIZONTAL
        tabView.setTextColor(createTextColorStateListByColorAttr())
        //tabView.isEnabled = false
        tab.customView = tabView
    }
}