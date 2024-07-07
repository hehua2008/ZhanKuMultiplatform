package com.hym.zhankumultiplatform.compose

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Density
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.hym.zhankumultiplatform.MyApplication

actual fun CharSequence.copyToClipboard() {
    val context = MyApplication.INSTANCE
    val clipboard = ContextCompat.getSystemService(context, ClipboardManager::class.java)!!
    val clipData = ClipData.newPlainText(null, this)
    clipboard.setPrimaryClip(clipData)
    Toast.makeText(context, "Copied: $this", Toast.LENGTH_SHORT).show()
}

actual fun String.htmlToPlainString(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
}

actual fun String.htmlToAnnotatedString(density: Density): AnnotatedString {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toAnnotatedString(density)
}
