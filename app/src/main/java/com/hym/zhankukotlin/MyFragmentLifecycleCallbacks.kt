package com.hym.zhankukotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

/**
 * @author hehua2008
 * @date 2022/3/23
 */
object MyFragmentLifecycleCallbacks : FragmentManager.FragmentLifecycleCallbacks() {
    const val TAG = "MyFragmentLifecycle"

    override fun onFragmentCreated(fm: FragmentManager, f: Fragment, savedInstanceState: Bundle?) {
        Log.d(TAG, "onFragmentCreated: $f")
    }

    override fun onFragmentViewCreated(
        fm: FragmentManager,
        f: Fragment,
        v: View,
        savedInstanceState: Bundle?
    ) {
        Log.d(TAG, "onFragmentViewCreated: $f")
    }

    override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
        Log.d(TAG, "onFragmentStarted: $f")
    }

    override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
        Log.d(TAG, "onFragmentResumed: $f")
    }

    override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
        Log.d(TAG, "onFragmentPaused: $f")
    }

    override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
        Log.d(TAG, "onFragmentStopped: $f")
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
        Log.d(TAG, "onFragmentViewDestroyed: $f")
    }

    override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
        Log.d(TAG, "onFragmentDestroyed: $f")
    }
}