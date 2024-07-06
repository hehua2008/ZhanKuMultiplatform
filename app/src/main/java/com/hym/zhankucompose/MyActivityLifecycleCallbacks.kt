package com.hym.zhankucompose

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

/**
 * @author hehua2008
 * @date 2022/3/23
 */
object MyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    const val TAG = "MyActivityLifecycle"

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(TAG, "onActivityCreated: $activity")
    }

    override fun onActivityStarted(activity: Activity) {
        Log.d(TAG, "onActivityStarted: $activity")
    }

    override fun onActivityResumed(activity: Activity) {
        Log.d(TAG, "onActivityResumed: $activity")
    }

    override fun onActivityPaused(activity: Activity) {
        Log.d(TAG, "onActivityPaused: $activity")
    }

    override fun onActivityStopped(activity: Activity) {
        Log.d(TAG, "onActivityStopped: $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        Log.d(TAG, "onActivityDestroyed: $activity")
    }
}