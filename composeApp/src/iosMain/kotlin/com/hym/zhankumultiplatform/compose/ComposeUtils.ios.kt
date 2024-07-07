package com.hym.zhankumultiplatform.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Density
import com.hym.zhankumultiplatform.util.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSAttributedString
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.dataUsingEncoding
import platform.UIKit.NSCharacterEncodingDocumentAttribute
import platform.UIKit.NSDocumentTypeDocumentAttribute
import platform.UIKit.NSHTMLTextDocumentType
import platform.UIKit.create

private const val TAG = "ComposeUtils"

actual fun CharSequence.copyToClipboard() {
    // TODO
}

@OptIn(ExperimentalForeignApi::class)
actual fun String.htmlToPlainString(): String {
    try {
        val data = (this as NSString).dataUsingEncoding(
            encoding = NSUTF8StringEncoding /*4UL NSStringEncoding.utf8*/,
            allowLossyConversion = true
        ) ?: return this

        val attributedString = NSAttributedString.create(
            data = data,
            options = mapOf(
                NSDocumentTypeDocumentAttribute /*NSAttributedStringDocumentReadingOptionKey.documentType*/ to NSHTMLTextDocumentType /*NSAttributedStringDocumentType.html*/,
                NSCharacterEncodingDocumentAttribute /*NSAttributedStringDocumentReadingOptionKey.characterEncoding*/ to NSUTF8StringEncoding /*4UL NSStringEncoding.utf8.rawValue*/
            ),
            documentAttributes = null,
            error = null
        ) ?: return this

        return attributedString.string
    } catch (e: Throwable) {
        Logger.e(TAG, "htmlToPlainString failed for $this", e)
        return this
    }
}

actual fun String.htmlToAnnotatedString(density: Density): AnnotatedString {
    // TODO
    return AnnotatedString(this)
}
