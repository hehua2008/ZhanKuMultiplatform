package com.hym.zhankucompose.ui.webview

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.R
import com.hym.zhankucompose.databinding.ActivityWebViewBinding

open class WebViewActivity : BaseActivity() {
    companion object {
        const val WEB_URL = "WEB_URL"
        const val WEB_TITLE = "WEB_TITLE"
    }

    protected lateinit var binding: ActivityWebViewBinding
    protected var url: String? = null
    protected var title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackClicked()
            }
        })

        super.onCreate(savedInstanceState)

        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.actionBar.setNavigationOnClickListener { finish() }
        binding.swipeRefresh.setOnRefreshListener { render() }

        binding.webView.settings.run {
            javaScriptEnabled = true
            domStorageEnabled = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        binding.webView.webViewClient = object : WebViewClient() {
            val customWebViewClient = initWebViewClient()

            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                setProgressVisible(true)
                customWebViewClient?.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                setProgressVisible(false)
                customWebViewClient?.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val scheme = request.url.scheme
                return when {
                    scheme == null
                            || scheme.equals("http", true)
                            || scheme.equals("https", true) -> {
                        false
                    }
                    else -> {
                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                        val resolves = packageManager.queryIntentActivities(intent, 0)
                        if (resolves.isNotEmpty()) {
                            startActivity(
                                Intent.createChooser(intent, getString(R.string.select_app_title))
                            )
                        }
                        true
                    }
                }
            }
        }
        binding.webView.webChromeClient = object : WebChromeClient() {
            val customWebChromeClient = initWebChromeClient()

            override fun onReceivedTitle(view: WebView, title: String?) {
                binding.actionBar.title = title
                customWebChromeClient?.onReceivedTitle(view, title)
            }

            override fun onProgressChanged(view: WebView, newProgress: Int) {
                binding.progressBar.progress = newProgress
                customWebChromeClient?.onProgressChanged(view, newProgress)
            }
        }

        checkIntent(intent)
    }

    private fun onBackClicked() {
        if (binding.webView.canGoBack()) {
            binding.webView.goBack()
        } else {
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    private fun checkIntent(intent: Intent) {
        url = intent.getStringExtra(WEB_URL)
        title = intent.getStringExtra(WEB_TITLE)
        render()
    }

    protected open fun render() {
        binding.actionBar.title = title
        url.takeIf {
            !it.isNullOrBlank()
        }?.let {
            binding.webView.loadUrl(it)
        }
    }

    protected open fun initWebViewClient(): WebViewClient? {
        return null
    }

    protected open fun initWebChromeClient(): WebChromeClient? {
        return null
    }

    protected fun setProgressVisible(visible: Boolean) {
        binding.swipeRefresh.isRefreshing = visible
        binding.progressBar.isVisible = visible
    }
}