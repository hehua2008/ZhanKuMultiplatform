package com.hym.zhankukotlin.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.hym.zhankukotlin.MyAppViewModel
import com.hym.zhankukotlin.databinding.ActivityMainBinding
import com.hym.zhankukotlin.getAppViewModel
import com.hym.zhankukotlin.model.TopCate

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
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

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        getAppViewModel<MyAppViewModel>().categoryItems.observe(this) { categoryItems ->
            mTopCates.clear()
            mTopCates.addAll(categoryItems)
            binding.viewPager.offscreenPageLimit = 1 + mTopCates.size
            sectionsPagerAdapter.setCategoryItems(mTopCates)
            binding.viewPager.currentItem = if (mTopCates.isEmpty()) 0 else 1
        }
    }
}