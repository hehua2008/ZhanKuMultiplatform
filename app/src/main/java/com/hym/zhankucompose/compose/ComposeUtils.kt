package com.hym.zhankucompose.compose

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

/**
 * @author hehua2008
 * @date 2024/3/8
 */
val EMPTY_BLOCK = {}
val EMPTY_COMPOSABLE_BLOCK = @Composable {}
val COMMON_PADDING = 6.dp
val PADDING_VALUES_ZERO = PaddingValues()
val BUTTON_CONTENT_PADDING = PaddingValues(8.dp)

fun CharSequence.copyToClipboard(context: Context) {
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)!!
    val clipData = ClipData.newPlainText(null, this)
    clipboard.setPrimaryClip(clipData)
    Toast.makeText(context, "Copied: $this", Toast.LENGTH_SHORT).show()
}
