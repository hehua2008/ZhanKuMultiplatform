package com.hym.zhankumultiplatform.player

import android.content.Context
import android.util.AttributeSet
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import com.hym.zhankumultiplatform.R
import kotlin.math.abs

class CustomPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {
    companion object {
        private const val TAG = "CustomPlayerView"

        private const val UNSET = -1
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
    }

    /**
     * The ratio information.
     */
    private var dimensionRatio: String? = null

    var widthHeightRatio: String?
        set(value) {
            parseDimensionRatioString(value)
            requestLayout()
        }
        get() = dimensionRatio

    /**
     * The ratio between the width and height.
     */
    private var widthHeightRatioValue = Float.NaN

    /**
     * The side to constrain using dimensRatio.
     */
    private var widthHeightRatioSide = VERTICAL

    init {
        // Ensure we are using the correctly themed context rather than the context that was
        // passed in.
        val ctx = getContext()
        val a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomPlayerView, defStyleAttr, 0)
        val widthHeightRatio = a.getString(R.styleable.CustomPlayerView_widthHeightRatio)
        parseDimensionRatioString(widthHeightRatio)
        a.recycle()
    }

    override fun setPlayer(newPlayer: Player?) {
        if (newPlayer == null) {
            player?.playerView = null
            super.setPlayer(null)
        } else {
            newPlayer.playerView = this
        }
    }

    fun superSetPlayer(player: Player?) {
        super.setPlayer(player)
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
            if (widthHeightRatioSide == HORIZONTAL) {
                if (measureWidth == 0 && measureHeight > 0) {
                    newWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (measureHeight * widthHeightRatioValue).toInt(),
                        modeHeight
                    )
                }
            } else if (widthHeightRatioSide == VERTICAL) {
                if (measureHeight == 0 && measureWidth > 0) {
                    newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        (measureWidth / widthHeightRatioValue).toInt(),
                        modeWidth
                    )
                }
            } else { // widthHeightRatioSide == UNSET
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
        var dimensionRatioSide = UNSET
        if (dimensionRatio != null) {
            val len = dimensionRatio.length
            var commaIndex = dimensionRatio.indexOf(',')
            if (commaIndex > 0 && commaIndex < len - 1) {
                val dimension = dimensionRatio.substring(0, commaIndex)
                if (dimension.equals("W", ignoreCase = true)) {
                    dimensionRatioSide = HORIZONTAL
                } else if (dimension.equals("H", ignoreCase = true)) {
                    dimensionRatioSide = VERTICAL
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
                                if (dimensionRatioSide == VERTICAL) {
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
        this.dimensionRatio = dimensionRatio
        this.widthHeightRatioValue = dimensionRatioValue
        this.widthHeightRatioSide = dimensionRatioSide
    }
}