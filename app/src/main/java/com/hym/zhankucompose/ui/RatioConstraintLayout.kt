package com.hym.zhankucompose.ui

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.hym.zhankucompose.R
import kotlin.math.abs

/**
 * @author hehua2008
 * @date 2021/12/13
 */
class RatioConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    companion object {
        private const val TAG = "RatioConstraintLayout"
    }

    /**
     * The ratio information.
     */
    private var widthHeightRatio: String? = null

    /**
     * The ratio between the width and height.
     */
    private var widthHeightRatioValue = Float.NaN

    /**
     * The side to constrain using dimensRatio.
     */
    private var widthHeightRatioSide = LayoutParams.VERTICAL

    init {
        // Ensure we are using the correctly themed context rather than the context that was
        // passed in.
        val ctx = getContext()
        val a = ctx.obtainStyledAttributes(
            attrs, R.styleable.RatioConstraintLayout, defStyleAttr, defStyleRes
        )
        val widthHeightRatio = a.getString(R.styleable.RatioConstraintLayout_widthHeightRatio)
        parseDimensionRatioString(widthHeightRatio)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (widthHeightRatioValue.isNaN()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            var newWidthMeasureSpec = widthMeasureSpec
            var newHeightMeasureSpec = heightMeasureSpec
            val modeWidth = MeasureSpec.getMode(widthMeasureSpec)
            val modeHeight = MeasureSpec.getMode(heightMeasureSpec)
            val measureWidth = MeasureSpec.getSize(widthMeasureSpec)
            val measureHeight = MeasureSpec.getSize(heightMeasureSpec)
            if (widthHeightRatioSide == ConstraintSet.HORIZONTAL) {
                if (measureWidth == 0 && measureHeight > 0) {
                    newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (measureHeight * widthHeightRatioValue).toInt(),
                        modeHeight
                    )
                }
            } else if (widthHeightRatioSide == ConstraintSet.VERTICAL) {
                if (measureHeight == 0 && measureWidth > 0) {
                    newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (measureWidth / widthHeightRatioValue).toInt(),
                        modeWidth
                    )
                }
            } else { // widthHeightRatioSide == ConstraintSet.UNSET
                if (measureWidth == 0 && measureHeight > 0) {
                    newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (measureHeight * widthHeightRatioValue).toInt(),
                        modeHeight
                    )
                } else if (measureHeight == 0 && measureWidth > 0) {
                    newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (measureWidth / widthHeightRatioValue).toInt(),
                        modeWidth
                    )
                }
            }
            super.onMeasure(newWidthMeasureSpec, newHeightMeasureSpec)
        }
    }

    private fun parseDimensionRatioString(dimensionRatio: String?) {
        var dimensionRatioValue = Float.NaN
        var dimensionRatioSide = ConstraintSet.UNSET
        if (dimensionRatio != null) {
            val len = dimensionRatio.length
            var commaIndex = dimensionRatio.indexOf(',')
            if (commaIndex > 0 && commaIndex < len - 1) {
                val dimension = dimensionRatio.substring(0, commaIndex)
                if (dimension.equals("W", ignoreCase = true)) {
                    dimensionRatioSide = ConstraintSet.HORIZONTAL
                } else if (dimension.equals("H", ignoreCase = true)) {
                    dimensionRatioSide = ConstraintSet.VERTICAL
                }
                commaIndex++
            } else {
                commaIndex = 0
            }
            val colonIndex = dimensionRatio.indexOf(':')
            if (colonIndex >= 0 && colonIndex < len - 1) {
                val nominator = dimensionRatio.substring(commaIndex, colonIndex)
                val denominator = dimensionRatio.substring(colonIndex + 1)
                if (nominator.isNotBlank() && denominator.isNotBlank()) {
                    try {
                        val nominatorValue = nominator.toFloat()
                        val denominatorValue = denominator.toFloat()
                        if (nominatorValue > 0 && denominatorValue > 0) {
                            dimensionRatioValue =
                                if (dimensionRatioSide == ConstraintSet.VERTICAL) {
                                    abs(denominatorValue / nominatorValue)
                                } else {
                                    abs(nominatorValue / denominatorValue)
                                }
                        }
                    } catch (e: NumberFormatException) {
                        // Ignore
                    }
                }
            } else {
                val r = dimensionRatio.substring(commaIndex)
                if (r.isNotBlank()) {
                    try {
                        dimensionRatioValue = r.toFloat()
                    } catch (e: NumberFormatException) {
                        // Ignore
                    }
                }
            }
        }
        this.widthHeightRatio = dimensionRatio
        this.widthHeightRatioValue = dimensionRatioValue
        this.widthHeightRatioSide = dimensionRatioSide
    }
}