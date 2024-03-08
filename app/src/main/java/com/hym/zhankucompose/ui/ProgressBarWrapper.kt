package com.hym.zhankucompose.ui

import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible

/**
 * This class wraps around two progress bars and is solely designed to fix
 * a bug in the framework (b/6928449) that prevents a progress bar from
 * gracefully switching back and forth between indeterminate and determinate
 * modes.
 */
class ProgressBarWrapper(
    private val determinate: ProgressBar,
    private val indeterminate: ProgressBar,
    private val isIndeterminate: Boolean
) {
    fun setVisibility(visibility: Int) {
        if (visibility == View.INVISIBLE || visibility == View.GONE) {
            indeterminate.visibility = visibility
            determinate.visibility = visibility
        } else {
            setVisible()
        }
    }

    private fun setVisible() {
        indeterminate.isVisible = isIndeterminate
        determinate.isVisible = !isIndeterminate
    }

    fun setMax(max: Int) {
        determinate.max = max
    }

    fun setProgress(progress: Int) {
        determinate.progress = progress
    }
}