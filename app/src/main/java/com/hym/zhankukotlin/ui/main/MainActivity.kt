package com.hym.zhankukotlin.ui.main

import android.os.Bundle
import android.os.Handler
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mMainHandler: Handler
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mSectionsPagerViewModel: SectionsPagerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mMainHandler = Handler()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        mBinding.viewPager.adapter = sectionsPagerAdapter
        mBinding.tabs.setupWithViewPager(mBinding.viewPager)
        mBinding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            private var mSelectedPosition = 0
            private val mResume = Runnable { MyApplication.imageLoader.resume() }

            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
                if (position != mSelectedPosition) {
                    // TODO
                }
            }

            override fun onPageSelected(position: Int) {
                mSelectedPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state != ViewPager.SCROLL_STATE_IDLE) {
                    mMainHandler.removeCallbacks(mResume)
                    MyApplication.imageLoader.pause()
                } else {
                    mMainHandler.postDelayed(
                        mResume, ViewConfiguration.getDoubleTapTimeout().toLong()
                    )
                }
            }
        })

        mSectionsPagerViewModel = ViewModelProvider(this).get(SectionsPagerViewModel::class.java)
        mSectionsPagerViewModel.categoryItems.observe(this, Observer { categoryItems ->
            mBinding.viewPager.offscreenPageLimit = categoryItems.size
            sectionsPagerAdapter.setCategoryItems(categoryItems)
        })

        mSectionsPagerViewModel.getCategoryItemsFromNetwork()
    }
}