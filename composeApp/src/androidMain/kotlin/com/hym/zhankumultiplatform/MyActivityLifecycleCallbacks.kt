package com.hym.zhankumultiplatform

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.hym.zhankumultiplatform.util.Logger

/**
 * @author hehua2008
 * @date 2022/3/23
 */
object MyActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    const val TAG = "MyActivityLifecycle"

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Logger.d(TAG, "onActivityCreated: $activity")
    }

    override fun onActivityStarted(activity: Activity) {
        Logger.d(TAG, "onActivityStarted: $activity")
    }

    override fun onActivityResumed(activity: Activity) {
        Logger.d(TAG, "onActivityResumed: $activity")
    }

    override fun onActivityPaused(activity: Activity) {
        Logger.d(TAG, "onActivityPaused: $activity")
    }

    override fun onActivityStopped(activity: Activity) {
        Logger.d(TAG, "onActivityStopped: $activity")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        Logger.d(TAG, "onActivityDestroyed: $activity")
    }
}