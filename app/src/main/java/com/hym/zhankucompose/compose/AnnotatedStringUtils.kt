package com.hym.zhankucompose.compose

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.ImageSpan
import android.text.style.LeadingMarginSpan
import android.text.style.LocaleSpan
import android.text.style.RelativeSizeSpan
import android.text.style.ScaleXSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.TextAppearanceSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

/**
 * @author hehua2008
 * @date 2024/3/17
 */
/**
 * https://issuetracker.google.com/issues/139320238
 */
fun Spanned.toAnnotatedString(density: Density): AnnotatedString {
    val spanned = this
    return buildAnnotatedString {
        append((spanned.toString()))
        spanned.getSpans(0, spanned.length, Any::class.java).forEach {
            val start = spanned.getSpanStart(it)
            val end = spanned.getSpanEnd(it)
            when (it) {
                is StyleSpan -> {
                    val spanStyle = when (it.style) {
                        Typeface.NORMAL -> SpanStyle(
                            fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Normal
                        )

                        Typeface.BOLD -> SpanStyle(
                            fontWeight = FontWeight.Bold,
                            //fontStyle = FontStyle.Normal
                        )

                        Typeface.ITALIC -> SpanStyle(
                            //fontWeight = FontWeight.Normal,
                            fontStyle = FontStyle.Italic
                        )

                        Typeface.BOLD_ITALIC -> SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic
                        )

                        else -> null
                    }

                    if (spanStyle != null) {
                        addStyle(spanStyle, start, end)
                    }
                }

                is TypefaceSpan -> addStyle(
                    SpanStyle(
                        fontFamily = when (it.family) {
                            FontFamily.SansSerif.name -> FontFamily.SansSerif
                            FontFamily.Serif.name -> FontFamily.Serif
                            FontFamily.Monospace.name -> FontFamily.Monospace
                            FontFamily.Cursive.name -> FontFamily.Cursive
                            else -> FontFamily.Default
                        }
                    ),
                    start,
                    end
                )

                is AbsoluteSizeSpan -> addStyle(
                    with(density) {
                        SpanStyle(fontSize = if (it.dip) it.size.dp.toSp() else it.size.toSp())
                    },
                    start,
                    end
                )

                is RelativeSizeSpan -> addStyle(
                    SpanStyle(fontSize = it.sizeChange.em),
                    start,
                    end
                )

                is StrikethroughSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.LineThrough),
                    start,
                    end
                )

                is UnderlineSpan -> addStyle(
                    SpanStyle(textDecoration = TextDecoration.Underline),
                    start,
                    end
                )

                is SuperscriptSpan -> addStyle(
                    SpanStyle(baselineShift = BaselineShift.Superscript),
                    start,
                    end
                )

                is SubscriptSpan -> addStyle(
                    SpanStyle(baselineShift = BaselineShift.Subscript),
                    start,
                    end
                )

                is ForegroundColorSpan -> addStyle(
                    SpanStyle(color = Color(it.foregroundColor)),
                    start,
                    end
                )

                is BackgroundColorSpan -> addStyle(
                    SpanStyle(background = Color(it.backgroundColor)),
                    start,
                    end
                )

                is TextAppearanceSpan -> {}

                is ScaleXSpan -> {}

                is LocaleSpan -> {}

                is LeadingMarginSpan -> {}

                is ImageSpan -> {} // ImageSpan -> DynamicDrawableSpan -> ReplacementSpan

                else -> {}
            }
        }
    }
}
