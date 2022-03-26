package com.hym.logcollector.util

import android.graphics.Color
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.MetricAffectingSpan

/**
 * @author hehua2008
 * @date 2021/8/17
 */
val LOG_KEYWORD_STYLES: List<CharacterStyle> = listOf(ForegroundColorSpan(Color.RED))

fun CharacterStyle.wrapper(): CharacterStyle =
    if (this is MetricAffectingSpan) MetricAffectingSpanWrapper(this)
    else CharacterStyleWrapper(this)

private class MetricAffectingSpanWrapper(private val mStyle: MetricAffectingSpan) :
    MetricAffectingSpan() {
    override fun updateDrawState(tp: TextPaint) = mStyle.updateDrawState(tp)

    override fun updateMeasureState(tp: TextPaint) = mStyle.updateMeasureState(tp)

    override fun getUnderlying(): MetricAffectingSpan = mStyle.underlying

    override fun hashCode(): Int = mStyle.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is MetricAffectingSpanWrapper) return false
        if (this === other) return true
        return mStyle == other.mStyle
    }
}

private class CharacterStyleWrapper(private val mStyle: CharacterStyle) : CharacterStyle() {
    override fun updateDrawState(tp: TextPaint) = mStyle.updateDrawState(tp)

    override fun getUnderlying(): CharacterStyle = mStyle.underlying

    override fun hashCode(): Int = mStyle.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is CharacterStyleWrapper) return false
        if (this === other) return true
        return mStyle == other.mStyle
    }
}