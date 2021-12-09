package com.hym.zhankukotlin.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hym.zhankukotlin.databinding.ActivityMainBinding
import com.hym.zhankukotlin.model.TopCate

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TOP_CATES = "TOP_CATES"
    }

    private val mTopCates: ArrayList<TopCate> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.getParcelableArrayList<TopCate>(TOP_CATES)?.let { mTopCates.addAll(it) }

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        sectionsPagerAdapter.setCategoryItems(mTopCates)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)

        val sectionsPagerViewModel = ViewModelProvider(this).get(SectionsPagerViewModel::class.java)
        sectionsPagerViewModel.categoryItems.observe(this) { categoryItems ->
            mTopCates.clear()
            mTopCates.addAll(categoryItems)
            binding.viewPager.offscreenPageLimit = mTopCates.size
            sectionsPagerAdapter.setCategoryItems(mTopCates)
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
}