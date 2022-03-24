package com.hym.zhankukotlin.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build

/**
 * @author hehua2008
 * @date 2021/12/31
 */

val Context.themeResId: Int
    @SuppressLint("DiscouragedPrivateApi")
    get() = Context::class.java.getDeclaredMethod("getThemeResId").invoke(this) as Int

fun Configuration.isNightMode(): Boolean {
    return uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}

fun Context.createOverrideContext(overrideConfig: Configuration? = null): Context {
    val config = Configuration(resources.configuration)
    overrideConfig?.let { config.updateFrom(it) }
    val overrideContext = createConfigurationContext(config)
    overrideContext.setTheme(themeResId)
    return overrideContext
}

fun Context.updateForNightMode(mode: Int): Boolean {
    val newNightMode: Int = when (mode) {
        Configuration.UI_MODE_NIGHT_YES -> Configuration.UI_MODE_NIGHT_YES
        Configuration.UI_MODE_NIGHT_NO -> Configuration.UI_MODE_NIGHT_NO
        else -> {
            // just use the system default from the application context
            val appConfig = applicationContext.resources.configuration
            appConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK
        }
    }
    val oldConfig = resources.configuration
    val currentNightMode = (oldConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK)
    if (currentNightMode != newNightMode) {
        // update the Resources with a new Configuration with an updated UI Mode
        val newConfig = Configuration(oldConfig).apply {
            uiMode = (newNightMode or (oldConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()))
        }
        updateResourcesConfiguration(newConfig)
        return true
    }
    return false
}

fun Context.updateResourcesConfiguration(newConfig: Configuration) {
    resources.updateConfiguration(newConfig, null)

    // We may need to flush the Resources' drawable cache due to framework bugs.
    if (Build.VERSION.SDK_INT < 26) {
        ResourcesFlusher.flush(resources)
    }
    val themeResId = themeResId
    if (themeResId != 0) {
        // We need to re-apply the theme so that it reflected the new
        // configuration
        setTheme(themeResId)
        if (Build.VERSION.SDK_INT >= 23) {
            // On M+ setTheme only applies if the themeResId actually changes,
            // since we have no way to publicly check what the Theme's current
            // themeResId is, we just manually apply it anyway. Most of the time
            // this is what we need anyway (since the themeResId does not
            // often change)
            theme.applyStyle(themeResId, true)
        }
    }
}
