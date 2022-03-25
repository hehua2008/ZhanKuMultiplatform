package com.hym.photoviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @author hehua2008
 * @date 2022/3/7
 */
class PhotoViewPagerAdapter(fm: FragmentManager, val photoInfos: List<UrlPhotoInfo>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val arguments = Bundle().apply {
            putInt(PhotoViewFragment.ARG_POSITION, position)
            putParcelable(PhotoViewFragment.ARG_PHOTO_INFO, photoInfos[position])
            putBoolean(PhotoViewFragment.ARG_WATCH_NETWORK, true)
        }
        return PhotoViewFragment.newInstance(arguments)
    }

    override fun getCount(): Int {
        return photoInfos.size
    }
}