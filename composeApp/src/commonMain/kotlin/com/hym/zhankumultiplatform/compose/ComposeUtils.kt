package com.hym.zhankumultiplatform.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

/**
 * @author hehua2008
 * @date 2024/3/8
 */
val EMPTY_BLOCK = {}
val EMPTY_COMPOSABLE_BLOCK = @Composable {}
val COMMON_PADDING = 6.dp
val PADDING_VALUES_ZERO = PaddingValues()
val SMALL_PADDING_VALUES = PaddingValues(4.dp)
val BUTTON_CONTENT_PADDING = PaddingValues(8.dp)
val SMALL_BUTTON_CONTENT_PADDING = PaddingValues(2.dp)

val NUMBER_REGEX = Regex("\\d*")
val NON_NUMBER_REGEX = Regex("\\D+")
val MULTIPLE_SPACE = Regex("\\s{2,}")

expect fun CharSequence.copyToClipboard()

expect fun String.htmlToPlainString(): String

expect fun String.htmlToAnnotatedString(density: Density): AnnotatedString
