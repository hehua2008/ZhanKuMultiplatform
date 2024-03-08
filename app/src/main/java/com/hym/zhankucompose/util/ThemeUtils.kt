@file:SuppressLint("RestrictedApi")

package com.hym.zhankucompose.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.widget.TintTypedArray
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import com.hym.zhankucompose.R
import kotlin.math.roundToInt

/**
 * @author hehua2008
 * @date 2022/1/27
 */

val DISABLED_STATE_SET = intArrayOf(-android.R.attr.state_enabled)
val FOCUSED_STATE_SET = intArrayOf(android.R.attr.state_focused)
val ACTIVATED_STATE_SET = intArrayOf(android.R.attr.state_activated)
val PRESSED_STATE_SET = intArrayOf(android.R.attr.state_pressed)
val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
val SELECTED_STATE_SET = intArrayOf(android.R.attr.state_selected)
val NOT_PRESSED_OR_FOCUSED_STATE_SET = intArrayOf(
    -android.R.attr.state_pressed, -android.R.attr.state_focused
)
val EMPTY_STATE_SET = IntArray(0)

/**
 * Creates a color state list from the provided colors.
 *
 * @param textColor Regular text color.
 * @param disabledTextColor Disabled text color.
 * @return Color state list.
 */
fun createDisabledStateList(
    @ColorInt textColor: Int, @ColorInt disabledTextColor: Int
): ColorStateList {
    // Now create a new ColorStateList with the default color, and the new disabled
    // color
    val states = arrayOfNulls<IntArray>(2)
    val colors = IntArray(2)
    var i = 0

    // Disabled state
    states[i] = DISABLED_STATE_SET
    colors[i] = disabledTextColor
    i++

    // Default state
    states[i] = EMPTY_STATE_SET
    colors[i] = textColor
    return ColorStateList(states, colors)
}

/**
 * Resolves the color from the provided theme attribute.
 *
 * @param attr Theme attribute for resolving color.
 * @return Resolved color.
 */
fun Context.getThemeAttrColor(@AttrRes attr: Int): Int {
    val tmp = IntArray(1)
    tmp[0] = attr
    val a = TintTypedArray.obtainStyledAttributes(this, null, tmp)
    return try {
        a.getColor(0, 0)
    } finally {
        a.recycle()
    }
}

/**
 * Resolves the color state list from the provided theme attribute.
 *
 * @param attr Theme attribute for resolving color state list.
 * @return Resolved color state list.
 */
fun Context.getThemeAttrColorStateList(@AttrRes attr: Int): ColorStateList? {
    val tmp = IntArray(1)
    tmp[0] = attr
    val a = TintTypedArray.obtainStyledAttributes(this, null, tmp)
    return try {
        a.getColorStateList(0)
    } finally {
        a.recycle()
    }
}

/**
 * Resolves the disabled color from the provided theme attribute.
 *
 * @param attr Theme attribute for resolving disabled color.
 * @return Resolved disabled color.
 */
fun Context.getDisabledThemeAttrColor(@AttrRes attr: Int): Int {
    val csl = getThemeAttrColorStateList(attr)
    return if (csl != null && csl.isStateful) {
        // If the CSL is stateful, we'll assume it has a disabled state and use it
        csl.getColorForState(DISABLED_STATE_SET, csl.defaultColor)
    } else {
        // Else, we'll generate the color using disabledAlpha from the theme
        val tv = TypedValue()
        // Now retrieve the disabledAlpha value from the theme
        theme.resolveAttribute(android.R.attr.disabledAlpha, tv, true)
        val disabledAlpha = tv.float
        getThemeAttrColor(attr, disabledAlpha)
    }
}

fun Context.getThemeAttrColor(@AttrRes attr: Int, alpha: Float): Int {
    val color = getThemeAttrColor(attr)
    val originalAlpha = Color.alpha(color)
    return ColorUtils.setAlphaComponent(color, (originalAlpha * alpha).roundToInt())
}

fun Context.createTextColorStateListByColorAttr(
    @AttrRes baseColorAttr: Int = R.attr.colorOnSurface,
    @AttrRes selectedColorAttr: Int = R.attr.colorPrimary
): ColorStateList {
    return createTextColorStateList(
        getThemeAttrColor(baseColorAttr),
        getThemeAttrColor(selectedColorAttr)
    )
}

fun Context.createTextColorStateListByColorRes(
    @ColorRes baseColorRes: Int, @ColorRes selectedColorRes: Int
): ColorStateList {
    return createTextColorStateList(
        ResourcesCompat.getColor(resources, baseColorRes, theme),
        ResourcesCompat.getColor(resources, selectedColorRes, theme)
    )
}

fun Context.createTextColorStateList(
    @ColorInt baseColor: Int, @ColorInt selectedColor: Int
): ColorStateList {
    val states = arrayOfNulls<IntArray>(3)
    val colors = IntArray(3)
    val disabledColor = getDisabledThemeAttrColor(R.attr.colorOnSurface)
    var i = 0
    states[i] = DISABLED_STATE_SET
    colors[i] = disabledColor
    ++i
    states[i] = SELECTED_STATE_SET
    colors[i] = selectedColor
    ++i
    states[i] = EMPTY_STATE_SET
    colors[i] = baseColor
    return ColorStateList(states, colors)
}
