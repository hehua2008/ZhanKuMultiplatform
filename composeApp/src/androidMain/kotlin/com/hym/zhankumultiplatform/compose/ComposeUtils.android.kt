package com.hym.zhankumultiplatform.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Density
import androidx.core.text.HtmlCompat

actual fun String.htmlToPlainString(): String {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
}

actual fun String.htmlToAnnotatedString(density: Density): AnnotatedString {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toAnnotatedString(density)
}
