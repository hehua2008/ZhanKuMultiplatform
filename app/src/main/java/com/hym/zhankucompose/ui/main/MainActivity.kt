package com.hym.zhankucompose.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hym.zhankucompose.BaseActivity

class MainActivity : BaseActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(false)
            }
        })

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            MainScreen()
        }
    }
}
