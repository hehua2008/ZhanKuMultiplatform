package com.hym.zhankukotlin

import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

/**
 * @author hehua2008
 * @date 2022/3/23
 */
open class BaseActivity : AppCompatActivity() {
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d(MyActivityLifecycleCallbacks.TAG, "onNewIntent: $this")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(MyActivityLifecycleCallbacks.TAG, "onConfigurationChanged: $this")
    }

    override fun getSystemService(name: String): Any? {
        return when (name) {
            WINDOW_SERVICE, SEARCH_SERVICE, LAYOUT_INFLATER_SERVICE -> super.getSystemService(name)
            else -> applicationContext.getSystemService(name)
        }
    }
}