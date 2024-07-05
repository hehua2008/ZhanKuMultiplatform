package com.hym.zhankucompose.util

import androidx.annotation.ColorInt
import androidx.annotation.Size
import kotlin.math.pow

/**
 * The Color class defines methods for creating and converting color ints.
 * Colors are represented as packed ints, made up of 4 bytes: alpha, red,
 * green, blue. The values are unpremultiplied, meaning any transparency is
 * stored solely in the alpha component, and not in the color components. The
 * components are stored as follows (alpha << 24) | (red << 16) |
 * (green << 8) | blue. Each component ranges between 0..255 with 0
 * meaning no contribution for that component, and 255 meaning 100%
 * contribution. Thus opaque-black would be 0xFF000000 (100% opaque but
 * no contributions from red, green, or blue), and opaque-white would be
 * 0xFFFFFFFF
 */
object Color {
    @ColorInt
    val BLACK: Int = -0x1000000

    @ColorInt
    val DKGRAY: Int = -0xbbbbbc

    @ColorInt
    val GRAY: Int = -0x777778

    @ColorInt
    val LTGRAY: Int = -0x333334

    @ColorInt
    val WHITE: Int = -0x1

    @ColorInt
    val RED: Int = -0x10000

    @ColorInt
    val GREEN: Int = -0xff0100

    @ColorInt
    val BLUE: Int = -0xffff01

    @ColorInt
    val YELLOW: Int = -0x100

    @ColorInt
    val CYAN: Int = -0xff0001

    @ColorInt
    val MAGENTA: Int = -0xff01

    @ColorInt
    val TRANSPARENT: Int = 0

    /**
     * Return the alpha component of a color int. This is the same as saying
     * color >>> 24
     */
    fun alpha(color: Int): Int {
        return color ushr 24
    }

    /**
     * Return the red component of a color int. This is the same as saying
     * (color >> 16) & 0xFF
     */
    fun red(color: Int): Int {
        return (color shr 16) and 0xFF
    }

    /**
     * Return the green component of a color int. This is the same as saying
     * (color >> 8) & 0xFF
     */
    fun green(color: Int): Int {
        return (color shr 8) and 0xFF
    }

    /**
     * Return the blue component of a color int. This is the same as saying
     * color & 0xFF
     */
    fun blue(color: Int): Int {
        return color and 0xFF
    }

    /**
     * Return a color-int from red, green, blue components.
     * The alpha component is implicity 255 (fully opaque).
     * These component values should be [0..255], but there is no
     * range check performed, so if they are out of range, the
     * returned color is undefined.
     * @param red  Red component [0..255] of the color
     * @param green Green component [0..255] of the color
     * @param blue  Blue component [0..255] of the color
     */
    @ColorInt
    fun rgb(red: Int, green: Int, blue: Int): Int {
        return (0xFF shl 24) or (red shl 16) or (green shl 8) or blue
    }

    /**
     * Return a color-int from alpha, red, green, blue components.
     * These component values should be [0..255], but there is no
     * range check performed, so if they are out of range, the
     * returned color is undefined.
     * @param alpha Alpha component [0..255] of the color
     * @param red   Red component [0..255] of the color
     * @param green Green component [0..255] of the color
     * @param blue  Blue component [0..255] of the color
     */
    @ColorInt
    fun argb(alpha: Int, red: Int, green: Int, blue: Int): Int {
        return (alpha shl 24) or (red shl 16) or (green shl 8) or blue
    }

    /**
     * Returns the relative luminance of a color.
     *
     *
     * Assumes sRGB encoding. Based on the formula for relative luminance
     * defined in WCAG 2.0, W3C Recommendation 11 December 2008.
     *
     * @return a value between 0 (darkest black) and 1 (lightest white)
     */
    fun luminance(@ColorInt color: Int): Float {
        var red = red(color) / 255.0
        red = if (red < 0.03928) red / 12.92 else ((red + 0.055) / 1.055).pow(2.4)
        var green = green(color) / 255.0
        green = if (green < 0.03928) green / 12.92 else ((green + 0.055) / 1.055).pow(2.4)
        var blue = blue(color) / 255.0
        blue = if (blue < 0.03928) blue / 12.92 else ((blue + 0.055) / 1.055).pow(2.4)
        return ((0.2126 * red) + (0.7152 * green) + (0.0722 * blue)).toFloat()
    }

    /**
     * Parse the color string, and return the corresponding color-int.
     * If the string cannot be parsed, throws an IllegalArgumentException
     * exception. Supported formats are:
     * #RRGGBB
     * #AARRGGBB
     * or one of the following names:
     * 'red', 'blue', 'green', 'black', 'white', 'gray', 'cyan', 'magenta',
     * 'yellow', 'lightgray', 'darkgray', 'grey', 'lightgrey', 'darkgrey',
     * 'aqua', 'fuchsia', 'lime', 'maroon', 'navy', 'olive', 'purple',
     * 'silver', 'teal'.
     */
    @ColorInt
    fun parseColor(@Size(min = 1) colorString: String): Int {
        if (colorString[0] == '#') {
            // Use a long to avoid rollovers on #ffXXXXXX
            var color = colorString.substring(1).toLong(16)
            if (colorString.length == 7) {
                // Set the alpha value
                color = color or 0x00000000ff000000L
            } else require(colorString.length == 9) { "Unknown color" }
            return color.toInt()
        } else {
            val color = sColorNameMap[colorString.lowercase()]
            if (color != null) {
                return color
            }
        }
        throw IllegalArgumentException("Unknown color")
    }

    private val sColorNameMap = mutableMapOf<String, Int>()

    init {
        sColorNameMap["black"] = BLACK
        sColorNameMap["darkgray"] = DKGRAY
        sColorNameMap["gray"] = GRAY
        sColorNameMap["lightgray"] = LTGRAY
        sColorNameMap["white"] = WHITE
        sColorNameMap["red"] = RED
        sColorNameMap["green"] = GREEN
        sColorNameMap["blue"] = BLUE
        sColorNameMap["yellow"] = YELLOW
        sColorNameMap["cyan"] = CYAN
        sColorNameMap["magenta"] = MAGENTA
        sColorNameMap["aqua"] = -0xff0001
        sColorNameMap["fuchsia"] = -0xff01
        sColorNameMap["darkgrey"] = DKGRAY
        sColorNameMap["grey"] = GRAY
        sColorNameMap["lightgrey"] = LTGRAY
        sColorNameMap["lime"] = -0xff0100
        sColorNameMap["maroon"] = -0x800000
        sColorNameMap["navy"] = -0xffff80
        sColorNameMap["olive"] = -0x7f8000
        sColorNameMap["purple"] = -0x7fff80
        sColorNameMap["silver"] = -0x3f3f40
        sColorNameMap["teal"] = -0xff7f80
    }
}
