package com.hym.zhankumultiplatform.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.hym.zhankumultiplatform.util.Logger
import kotlinx.cinterop.BooleanVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGColorGetAlpha
import platform.CoreGraphics.CGColorGetComponents
import platform.Foundation.NSArray
import platform.Foundation.NSAttributedString
import platform.Foundation.NSAttributedStringEnumerationOptions
import platform.Foundation.NSMakeRange
import platform.Foundation.NSNumber
import platform.Foundation.NSRange
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.attributedSubstringFromRange
import platform.Foundation.dataUsingEncoding
import platform.Foundation.enumerateAttributesInRange
import platform.Foundation.length
import platform.UIKit.NSAttachmentAttributeName
import platform.UIKit.NSBackgroundColorAttributeName
import platform.UIKit.NSBaselineOffsetAttributeName
import platform.UIKit.NSCharacterEncodingDocumentAttribute
import platform.UIKit.NSDocumentTypeDocumentAttribute
import platform.UIKit.NSFontAttributeName
import platform.UIKit.NSForegroundColorAttributeName
import platform.UIKit.NSHTMLTextDocumentType
import platform.UIKit.NSKernAttributeName
import platform.UIKit.NSLigatureAttributeName
import platform.UIKit.NSLineBreakByCharWrapping
import platform.UIKit.NSLineBreakByClipping
import platform.UIKit.NSLineBreakByTruncatingHead
import platform.UIKit.NSLineBreakByTruncatingMiddle
import platform.UIKit.NSLineBreakByTruncatingTail
import platform.UIKit.NSLineBreakByWordWrapping
import platform.UIKit.NSLineBreakMode
import platform.UIKit.NSLinkAttributeName
import platform.UIKit.NSParagraphStyle
import platform.UIKit.NSParagraphStyleAttributeName
import platform.UIKit.NSShadow
import platform.UIKit.NSShadowAttributeName
import platform.UIKit.NSStrikethroughColorAttributeName
import platform.UIKit.NSStrikethroughStyleAttributeName
import platform.UIKit.NSStrokeColorAttributeName
import platform.UIKit.NSStrokeWidthAttributeName
import platform.UIKit.NSTextAlignment
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.NSTextAlignmentJustified
import platform.UIKit.NSTextAlignmentLeft
import platform.UIKit.NSTextAlignmentNatural
import platform.UIKit.NSTextAlignmentRight
import platform.UIKit.NSTextAttachment
import platform.UIKit.NSTextEffectAttributeName
import platform.UIKit.NSUnderlineColorAttributeName
import platform.UIKit.NSUnderlineStyleAttributeName
import platform.UIKit.NSWritingDirection
import platform.UIKit.NSWritingDirectionAttributeName
import platform.UIKit.NSWritingDirectionLeftToRight
import platform.UIKit.NSWritingDirectionNatural
import platform.UIKit.NSWritingDirectionRightToLeft
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UIFontDescriptorSymbolicTraits
import platform.UIKit.UIFontDescriptorTraitItalic
import platform.UIKit.UIFontDescriptorTraitKey
import platform.UIKit.UIFontDescriptorTraitsAttribute
import platform.UIKit.UIFontWeight
import platform.UIKit.UIFontWeightBlack
import platform.UIKit.UIFontWeightBold
import platform.UIKit.UIFontWeightHeavy
import platform.UIKit.UIFontWeightLight
import platform.UIKit.UIFontWeightMedium
import platform.UIKit.UIFontWeightRegular
import platform.UIKit.UIFontWeightSemibold
import platform.UIKit.UIFontWeightThin
import platform.UIKit.UIFontWeightTrait
import platform.UIKit.UIFontWeightUltraLight
import platform.UIKit.create

private const val TAG = "ComposeUtils"

actual fun String.htmlToPlainString(): String {
    return toNSAttributedString()?.string ?: this
}

actual fun String.htmlToAnnotatedString(density: Density): AnnotatedString {
    return toNSAttributedString()?.toAnnotatedString(density) ?: AnnotatedString(this)
}

@OptIn(ExperimentalForeignApi::class)
fun String.toNSAttributedString(): NSAttributedString? {
    try {
        val data = (this as NSString).dataUsingEncoding(
            encoding = NSUTF8StringEncoding /*4UL NSStringEncoding.utf8*/,
            allowLossyConversion = true
        ) ?: return null

        val attributedString = NSAttributedString.create(
            data = data,
            options = mapOf(
                NSDocumentTypeDocumentAttribute /*NSAttributedStringDocumentReadingOptionKey.documentType*/ to NSHTMLTextDocumentType /*NSAttributedStringDocumentType.html*/,
                NSCharacterEncodingDocumentAttribute /*NSAttributedStringDocumentReadingOptionKey.characterEncoding*/ to NSUTF8StringEncoding /*4UL NSStringEncoding.utf8.rawValue*/
            ),
            documentAttributes = null,
            error = null
        ) ?: return null

        return attributedString
    } catch (e: Throwable) {
        Logger.e(TAG, "toNSAttributedString failed for $this", e)
        return null
    }
}

@OptIn(ExperimentalForeignApi::class)
fun NSAttributedString.toAnnotatedString(density: Density): AnnotatedString {
    /*
        /************************ Attributes ************************/
    @available(iOS 6.0, *)
    public static let font: NSAttributedString.Key

    @available(iOS 6.0, *)
    public static let paragraphStyle: NSAttributedString.Key // NSParagraphStyle, default defaultParagraphStyle

    @available(iOS 6.0, *)
    public static let foregroundColor: NSAttributedString.Key // UIColor, default blackColor

    @available(iOS 6.0, *)
    public static let backgroundColor: NSAttributedString.Key // UIColor, default nil: no background

    @available(iOS 6.0, *)
    public static let ligature: NSAttributedString.Key // NSNumber containing integer, default 1: default ligatures, 0: no ligatures

    @available(iOS 6.0, *)
    public static let kern: NSAttributedString.Key // NSNumber containing floating point value, in points; amount to modify default kerning. 0 means kerning is disabled.

    @available(iOS 14.0, *)
    public static let tracking: NSAttributedString.Key // NSNumber containing floating point value, in points; amount to modify default tracking. 0 means tracking is disabled.

    @available(iOS 6.0, *)
    public static let strikethroughStyle: NSAttributedString.Key // NSNumber containing integer, default 0: no strikethrough

    @available(iOS 6.0, *)
    public static let underlineStyle: NSAttributedString.Key // NSNumber containing integer, default 0: no underline

    @available(iOS 6.0, *)
    public static let strokeColor: NSAttributedString.Key // UIColor, default nil: same as foreground color

    @available(iOS 6.0, *)
    public static let strokeWidth: NSAttributedString.Key // NSNumber containing floating point value, in percent of font point size, default 0: no stroke; positive for stroke alone, negative for stroke and fill (a typical value for outlined text would be 3.0)

    @available(iOS 6.0, *)
    public static let shadow: NSAttributedString.Key // NSShadow, default nil: no shadow

    @available(iOS 7.0, *)
    public static let textEffect: NSAttributedString.Key // NSString, default nil: no text effect

    @available(iOS 7.0, *)
    public static let attachment: NSAttributedString.Key // NSTextAttachment, default nil

    @available(iOS 7.0, *)
    public static let link: NSAttributedString.Key // NSURL (preferred) or NSString

    @available(iOS 7.0, *)
    public static let baselineOffset: NSAttributedString.Key // NSNumber containing floating point value, in points; offset from baseline, default 0

    @available(iOS 7.0, *)
    public static let underlineColor: NSAttributedString.Key // UIColor, default nil: same as foreground color

    @available(iOS 7.0, *)
    public static let strikethroughColor: NSAttributedString.Key // UIColor, default nil: same as foreground color

    @available(iOS 7.0, *)
    public static let writingDirection: NSAttributedString.Key // NSArray of NSNumbers representing the nested levels of writing direction overrides as defined by Unicode LRE, RLE, LRO, and RLO characters.  The control characters can be obtained by masking NSWritingDirection and NSWritingDirectionFormatType values.  LRE: NSWritingDirectionLeftToRight|NSWritingDirectionEmbedding, RLE: NSWritingDirectionRightToLeft|NSWritingDirectionEmbedding, LRO: NSWritingDirectionLeftToRight|NSWritingDirectionOverride, RLO: NSWritingDirectionRightToLeft|NSWritingDirectionOverride,
    */

    val enumerationRange: CValue<NSRange> = NSMakeRange(loc = 0UL, len = length)
    val options: NSAttributedStringEnumerationOptions /* = ULong */ = 0UL
    val builder = AnnotatedString.Builder()

    enumerateAttributesInRange(
        enumerationRange = enumerationRange,
        options = options
    ) { attributes: Map<Any?, *>?, range: CValue<NSRange>, bool: CPointer<BooleanVar>? /* = BooleanVarOf<Boolean> */ ->
        val plainString = attributedSubstringFromRange(range = range).string
        val start = range.useContents {
            location.toInt()
        }
        val end = range.useContents {
            (location + length).toInt()
        }

        var spanStyle: SpanStyle? = null
        var paragraphStyle: ParagraphStyle? = null

        attributes?.forEach { (key, value) ->
            when (key) {
                NSFontAttributeName -> {
                    value as UIFont
                    spanStyle = SpanStyle(
                        color = Color.Unspecified,
                        // TODO: TextUnit(value.pointSize.toFloat(), TextUnitType.Sp),
                        fontSize = TextUnit.Unspecified,
                        fontWeight = value.getFontWeight(),
                        fontStyle = value.getFontStyle(),
                        fontSynthesis = null,
                        fontFamily = value.getFontFamily(),
                        fontFeatureSettings = null,
                        letterSpacing = TextUnit.Unspecified,
                        baselineShift = null,
                        textGeometricTransform = null,
                        localeList = null,
                        background = Color.Unspecified,
                        textDecoration = null,
                        shadow = null,
                        platformStyle = null,
                        drawStyle = null
                    ).merge(spanStyle)
                }

                NSParagraphStyleAttributeName -> {
                    // default defaultParagraphStyle
                    value as NSParagraphStyle
                    paragraphStyle = ParagraphStyle(
                        textAlign = value.alignment.toTextAlign(),
                        textDirection = value.baseWritingDirection.toTextDirection(),
                        lineHeight = TextUnit.Unspecified, // TODO
                        textIndent = TextIndent(
                            firstLine = TextUnit(
                                value = value.firstLineHeadIndent.toFloat(),
                                type = TextUnitType.Sp
                            ),
                            restLine = TextUnit(
                                value = value.headIndent.toFloat(),
                                type = TextUnitType.Sp
                            )
                        ),
                        platformStyle = null,
                        lineHeightStyle = null,
                        lineBreak = value.lineBreakMode.toLineBreak(),
                        hyphens = Hyphens.Unspecified,
                        textMotion = null
                    ).merge(paragraphStyle)
                }

                NSForegroundColorAttributeName -> {
                    // default blackColor
                    (value as UIColor).toColor()?.let {
                        spanStyle = SpanStyle(color = it).merge(spanStyle)
                    }
                }

                NSBackgroundColorAttributeName -> {
                    // default nil: no background
                    (value as UIColor?)?.toColor()?.let {
                        spanStyle = SpanStyle(background = it).merge(spanStyle)
                    }
                }

                NSLigatureAttributeName -> {
                    // integer, default 1: default ligatures, 0: no ligatures
                    (value as NSNumber).intValue
                }

                NSKernAttributeName -> {
                    // floating point value, in points; amount to modify default kerning. 0 means kerning is disabled.
                    (value as NSNumber).floatValue
                }

                /*
                NSTrackingAttributeName -> {
                    value as NSNumber
                }
                */

                NSStrikethroughStyleAttributeName -> {
                    // integer, default 0: no strikethrough
                    val intValue = (value as NSNumber).intValue
                    if (intValue != 0) {
                        spanStyle = SpanStyle(textDecoration = TextDecoration.LineThrough)
                            .merge(spanStyle)
                    }
                }

                NSUnderlineStyleAttributeName -> {
                    // integer, default 0: no underline
                    val intValue = (value as NSNumber).intValue
                    if (intValue != 0) {
                        spanStyle = SpanStyle(textDecoration = TextDecoration.Underline)
                            .merge(spanStyle)
                    }
                }

                NSStrokeColorAttributeName -> {
                    // default nil: same as foreground color
                    value as UIColor?
                }

                NSStrokeWidthAttributeName -> {
                    // floating point value, in percent of font point size, default 0: no stroke;
                    // positive for stroke alone, negative for stroke and fill (a typical value for outlined text would be 3.0)
                    val floatValue = (value as NSNumber).floatValue
                    if (floatValue != 0f) {
                        spanStyle = SpanStyle(drawStyle = Stroke(width = floatValue))
                            .merge(spanStyle)
                    }
                }

                NSShadowAttributeName -> {
                    // default nil: no shadow
                    (value as NSShadow?)?.let {
                        val color = (it.shadowColor as UIColor?)?.toColor() ?: return@let
                        val offset = it.shadowOffset.useContents {
                            Offset(x = width.toFloat(), y = height.toFloat())
                        }
                        val blurRadius = it.shadowBlurRadius.toFloat()
                        spanStyle = SpanStyle(
                            shadow = Shadow(color = color, offset = offset, blurRadius = blurRadius)
                        ).merge(spanStyle)
                    }
                }

                NSTextEffectAttributeName -> {
                    // default nil: no text effect
                    value as NSString?
                }

                NSAttachmentAttributeName -> {
                    // default nil
                    value as NSTextAttachment?
                }

                NSLinkAttributeName -> {
                    when (value) {
                        NSURL -> {}

                        NSString -> {}
                    }
                }

                NSBaselineOffsetAttributeName -> {
                    // floating point value, in points; offset from baseline, default 0
                    val floatValue = (value as NSNumber).floatValue
                    if (floatValue != 0f) {
                        spanStyle = SpanStyle(baselineShift = BaselineShift(floatValue))
                            .merge(spanStyle)
                    }
                }

                NSUnderlineColorAttributeName -> {
                    // default nil: same as foreground color
                    value as UIColor?
                }

                NSStrikethroughColorAttributeName -> {
                    // default nil: same as foreground color
                    value as UIColor?
                }

                NSWritingDirectionAttributeName -> {
                    value as NSArray/*<NSNumber>*/
                }
            }
        }

        builder.append(plainString)
        spanStyle?.let { builder.addStyle(it, start, end) }
        paragraphStyle?.let { builder.addStyle(it, start, end) }
    }

    return builder.toAnnotatedString()
}

fun UIFont.getFontFamily(): FontFamily {
    return when (familyName) {
        FontFamily.SansSerif.name -> FontFamily.SansSerif
        FontFamily.Serif.name -> FontFamily.Serif
        FontFamily.Monospace.name -> FontFamily.Monospace
        FontFamily.Cursive.name -> FontFamily.Cursive
        else -> FontFamily.Default
    }
}

fun UIFont.getFontWeight(): FontWeight {
    val traitAttributes =
        fontDescriptor.fontAttributes[UIFontDescriptorTraitsAttribute] as? Map<UIFontDescriptorTraitKey, *>
    if (traitAttributes != null) {
        val fontWeight = traitAttributes[UIFontWeightTrait] as? UIFontWeight
        if (fontWeight != null) {
            return fontWeight.toFontWeight()
        }
    }

    val variationAttribute =
        fontDescriptor.fontAttributes["NSCTFontVariationAttribute"] as? Map<Int, Float>
    if (variationAttribute != null) {
        val weightKey = 2003265652
        val weight = variationAttribute[weightKey]
        if (weight != null) {
            return when (weight) {
                100f -> FontWeight.W100
                200f -> FontWeight.W200
                300f -> FontWeight.W300
                400f -> FontWeight.W400
                500f -> FontWeight.W500
                600f -> FontWeight.W600
                700f -> FontWeight.W700
                800f -> FontWeight.W800
                900f -> FontWeight.W900
                else -> FontWeight(weight.toInt())
            }
        }
    }

    val fontUsage = fontDescriptor.fontAttributes["NSCTFontUIUsageAttribute"] as? String
    if (fontUsage != null) {
        return when (fontUsage) {
            "CTFontUltraLightUsage" -> FontWeight.ExtraLight
            "CTFontThinUsage" -> FontWeight.Thin
            "CTFontLightUsage" -> FontWeight.Light
            "CTFontRegularUsage" -> FontWeight.Normal
            "CTFontMediumUsage" -> FontWeight.Medium
            "CTFontDemiUsage" -> FontWeight.SemiBold
            "CTFontBoldUsage" -> FontWeight.Bold
            "CTFontHeavyUsage" -> FontWeight.ExtraBold
            "CTFontBlackUsage" -> FontWeight.Black
            else -> FontWeight.Normal
        }
    }

    return FontWeight.Normal
}

fun UIFontWeight.toFontWeight(): FontWeight {
    return when (this) {
        UIFontWeightUltraLight -> FontWeight.ExtraLight
        UIFontWeightThin -> FontWeight.Thin
        UIFontWeightLight -> FontWeight.Light
        UIFontWeightRegular -> FontWeight.Normal
        UIFontWeightMedium -> FontWeight.Medium
        UIFontWeightSemibold -> FontWeight.SemiBold
        UIFontWeightBold -> FontWeight.Bold
        UIFontWeightHeavy -> FontWeight.ExtraBold
        UIFontWeightBlack -> FontWeight.Black
        else -> FontWeight.Normal
    }
}

fun UIFont.getFontStyle(): FontStyle {
    val symbolicTraits: UIFontDescriptorSymbolicTraits = fontDescriptor.symbolicTraits
    if (symbolicTraits and UIFontDescriptorTraitItalic != 0u) {
        return FontStyle.Italic
    }
    return FontStyle.Normal
}

fun NSLineBreakMode.toLineBreak(): LineBreak {
    return when (this) {
        NSLineBreakByWordWrapping -> LineBreak.Simple
        NSLineBreakByCharWrapping,
        NSLineBreakByClipping,
        NSLineBreakByTruncatingHead,
        NSLineBreakByTruncatingMiddle,
        NSLineBreakByTruncatingTail -> LineBreak.Unspecified

        else -> LineBreak.Unspecified
    }
}

fun NSTextAlignment.toTextAlign(): TextAlign {
    return when (this) {
        NSTextAlignmentLeft -> TextAlign.Left
        NSTextAlignmentCenter -> TextAlign.Center
        NSTextAlignmentRight -> TextAlign.Right
        NSTextAlignmentJustified -> TextAlign.Justify
        NSTextAlignmentNatural -> TextAlign.Unspecified
        else -> TextAlign.Unspecified
    }
}

fun NSWritingDirection.toTextDirection(): TextDirection {
    return when (this) {
        NSWritingDirectionLeftToRight -> TextDirection.Ltr
        NSWritingDirectionRightToLeft -> TextDirection.Rtl
        NSWritingDirectionNatural -> TextDirection.Content
        else -> TextDirection.Unspecified
    }
}

@OptIn(ExperimentalForeignApi::class)
fun UIColor.toColor(): Color? {
    val cgColor = this.CGColor ?: return null
    val components = CGColorGetComponents(cgColor) ?: return null
    val red = components[0].toFloat()
    val green = components[1].toFloat()
    val blue = components[2].toFloat()
    val alpha = CGColorGetAlpha(cgColor).toFloat()
    return Color(red = red, green = green, blue = blue, alpha = alpha)
}
