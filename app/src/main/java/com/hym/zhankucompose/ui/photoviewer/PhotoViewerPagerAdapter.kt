package com.hym.zhankucompose.ui.photoviewer

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.hym.zhankucompose.photo.UrlPhotoInfo

/**
 * @author hehua2008
 * @date 2022/3/7
 */
class PhotoViewerPagerAdapter(fm: FragmentManager, val photoInfos: List<UrlPhotoInfo>) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        val arguments = Bundle().apply {
            putInt(PhotoViewerFragment.ARG_POSITION, position)
            putParcelable(PhotoViewerFragment.ARG_PHOTO_INFO, photoInfos[position])
            putBoolean(PhotoViewerFragment.ARG_WATCH_NETWORK, true)
        }
        return PhotoViewerFragment.newInstance(arguments)
    }

    override fun getCount(): Int {
        return photoInfos.size
    }
}