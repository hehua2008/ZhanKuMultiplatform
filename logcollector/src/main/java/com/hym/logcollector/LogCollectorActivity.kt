package com.hym.logcollector

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.hym.logcollector.base.LogConfig
import com.hym.logcollector.databinding.ActivityLogCollectorBinding
import com.hym.logcollector.ui.SectionsPagerAdapter

class LogCollectorActivity : AppCompatActivity() {
    companion object {
        const val LOG_CONFIG = "LOG_CONFIG"
        private const val TAG = "LogCollectorActivity"

        @JvmStatic
        fun navigate(context: Context, logConfig: LogConfig? = null) {
            val intent = Intent(context, LogCollectorActivity::class.java)
                .putExtra(LOG_CONFIG, logConfig)
            context.startActivity(intent)
        }
    }

    private val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
    private lateinit var mLogConfig: LogConfig

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(LOG_CONFIG, mLogConfig)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        super.onCreate(savedInstanceState)

        val logConfig = savedInstanceState?.getParcelable(LOG_CONFIG)
            ?: intent.getParcelableExtra(LOG_CONFIG) ?: LogcatService.startLogConfig
        if (logConfig == null) {
            Log.e(TAG, "logConfig is null !")
            finish()
            return
        }
        mLogConfig = logConfig

        val binding = ActivityLogCollectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
        sectionsPagerAdapter.setLogConfig(logConfig)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val logConfig = intent.getParcelableExtra(LOG_CONFIG) ?: LogcatService.startLogConfig
        if (logConfig == null) {
            Log.e(TAG, "logConfig is null !")
            finish()
            return
        }
        mLogConfig = logConfig
        sectionsPagerAdapter.setLogConfig(logConfig)
    }
}