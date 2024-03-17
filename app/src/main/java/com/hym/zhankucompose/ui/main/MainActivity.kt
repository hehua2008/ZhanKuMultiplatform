package com.hym.zhankucompose.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.MyAppViewModel
import com.hym.zhankucompose.databinding.ActivityMainBinding
import com.hym.zhankucompose.getAppViewModel
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.TabReselectedCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class MainActivity : BaseActivity(), OnTabSelectedListener {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
    private val mTopCates: MutableList<TopCate> = mutableListOf()

    private var mInitialFragmentPosition = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(false)
            }
        })
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val distance = abs(position - mInitialFragmentPosition)
                if (binding.viewPager.offscreenPageLimit < distance) {
                    binding.viewPager.offscreenPageLimit = distance
                }
            }

            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                val maxDistance = abs(position - mInitialFragmentPosition).let {
                    when {
                        positionOffset == 0f -> it // Only position is visible
                        position == sectionsPagerAdapter.count - 1 -> it // Last position
                        else -> it.coerceAtLeast(abs(position + 1 - mInitialFragmentPosition))
                    }
                }
                if (binding.viewPager.offscreenPageLimit < maxDistance) {
                    binding.viewPager.offscreenPageLimit = maxDistance
                }
            }
        })
        binding.tabs.setupWithViewPager(binding.viewPager)
        binding.tabs.addOnTabSelectedListener(this)

        getAppViewModel<MyAppViewModel>().categoryItems.observe(this) { categoryItems ->
            mTopCates.clear()
            mTopCates.addAll(categoryItems)
            if (binding.viewPager.offscreenPageLimit < 1) {
                binding.viewPager.offscreenPageLimit = 1
            }
            sectionsPagerAdapter.setCategoryItems(mTopCates)
            binding.viewPager.currentItem = if (mTopCates.isEmpty()) 0 else mInitialFragmentPosition
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
        sectionsPagerAdapter.currentFragment?.let {
            if (it is TabReselectedCallback) {
                it.onTabReselected()
            }
        }
    }
}