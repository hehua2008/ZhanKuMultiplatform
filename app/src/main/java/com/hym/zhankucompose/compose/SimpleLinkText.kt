package com.hym.zhankucompose.compose

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle

/**
 * @author hehua2008
 * @date 2024/3/14
 */
// Google link color, Light = R:19 G:27 B:164 , Night = R:146 G:181 B:243
val LightLinkSpanStyle =
    SpanStyle(color = Color(0xFF131BA4), textDecoration = TextDecoration.Underline)
val NightLinkSpanStyle =
    SpanStyle(color = Color(0xFF92B5F3), textDecoration = TextDecoration.Underline)

@Composable
fun SimpleLinkText(
    link: String,
    text: String = link,
    modifier: Modifier = Modifier,
    spanStyle: SpanStyle = if (isSystemInDarkTheme()) NightLinkSpanStyle else LightLinkSpanStyle,
    style: TextStyle = LocalTextStyle.current,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    maxLines: Int = 1,
    onLinkClick: (link: String) -> Unit
) {
    val annotatedText = remember(link, text) {
        buildAnnotatedString {
            // We attach this *URL* annotation to the following content until `pop()` is called
            pushStringAnnotation(tag = "URL", annotation = link)
            withStyle(spanStyle) {
                append(text)
            }
            pop()
        }
    }

    ClickableText(
        text = annotatedText,
        modifier = modifier,
        style = style,
        overflow = overflow,
        maxLines = maxLines
    ) { offset ->
        // We check if there is an *URL* annotation attached to the text
        // at the clicked position
        annotatedText.getStringAnnotations(
            tag = "URL", start = offset, end = offset
        ).firstOrNull()?.let { range ->
            onLinkClick(range.item)
        }
    }
}
