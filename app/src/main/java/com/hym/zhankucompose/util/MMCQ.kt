package com.hym.zhankucompose.util

import androidx.annotation.IntDef
import androidx.compose.ui.graphics.ImageBitmap
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Created by hehua2008 on 9/20/18. Blog: https://blog.csdn.net/hegan2010/article/details/84308152
 *
 * Modified Median Cut Quantization(MMCQ) Leptonica: http://tpgit.github
 * .io/UnOfficialLeptDocs/leptonica/color-quantization.html
 */
class MMCQ(
    bitmap: ImageBitmap,
    maxColor: Int,
    fraction: Double = 0.85,
    sigbits: Int = 5
) {
    @IntDef(*[COLOR_ALPHA, COLOR_RED, COLOR_GREEN, COLOR_BLUE])
    @Retention(AnnotationRetention.SOURCE)
    annotation class ColorPart

    private val mPixelRGB: IntArray
    private val mMaxColor: Int
    private var mFraction = 0.85
    private var mSigbits = 5
    private var mRshift = 8 - mSigbits
    private val mWidth: Int
    private val mHeight: Int
    private val mPixHisto = mutableMapOf<Int, Int>()


    private fun initPixHisto() {
        for (color in mPixelRGB) {
            val alpha = Color.alpha(color)
            if (alpha < 128) {
                continue
            }
            val red = Color.red(color) shr mRshift
            val green = Color.green(color) shr mRshift
            val blue = Color.blue(color) shr mRshift
            val colorIndex = getColorIndexWithRgb(red, green, blue)
            val count = mPixHisto[colorIndex] ?: 0
            mPixHisto.put(colorIndex, count + 1)
        }
    }

    private fun createVBox(): VBox {
        val rMax = getMax(COLOR_RED) shr mRshift
        val rMin = getMin(COLOR_RED) shr mRshift
        val gMax = getMax(COLOR_GREEN) shr mRshift
        val gMin = getMin(COLOR_GREEN) shr mRshift
        val bMax = getMax(COLOR_BLUE) shr mRshift
        val bMin = getMin(COLOR_BLUE) shr mRshift

        return VBox(rMin, rMax, gMin, gMax, bMin, bMax, 1 shl mRshift, mPixHisto)
    }

    private fun getMax(@ColorPart which: Int): Int {
        var max = 0
        for (color in mPixelRGB) {
            val value = getColorPart(color, which)
            if (max < value) {
                max = value
            }
        }
        return max
    }

    private fun getMin(@ColorPart which: Int): Int {
        var min = Int.MAX_VALUE
        for (color in mPixelRGB) {
            val value = getColorPart(color, which)
            if (min > value) {
                min = value
            }
        }
        return min
    }

    fun quantize(): List<ThemeColor> {
        require(mWidth * mHeight >= mMaxColor) { "Image({$mWidth}x{$mHeight}) too small to be quantized" }

        val oriVBox = createVBox()
        val pOneQueue = PriorityQueue<VBox>(mMaxColor)
        pOneQueue.offer(oriVBox)
        val popColors = (mMaxColor * mFraction).toInt()
        iterCut(popColors, pOneQueue)

        val boxQueue = PriorityQueue<VBox>(mMaxColor) { o1, o2 ->
            val priority1: Long = o1.priority * o1.mVolume
            val priority2: Long = o2.priority * o2.mVolume
            priority1.compareTo(priority2)
        }

        boxQueue.addAll(pOneQueue)
        pOneQueue.clear()

        iterCut(mMaxColor - popColors + 1, boxQueue)

        pOneQueue.addAll(boxQueue)
        boxQueue.clear()

        val themeColors = PriorityQueue<ThemeColor>(mMaxColor)

        while (true) {
            val vBox = pOneQueue.poll() ?: break
            val proportion = vBox.mNumPixs.toDouble() / oriVBox.mNumPixs
            if (proportion < 0.05) {
                continue
            }
            val themeColor = ThemeColor(vBox.avgColor, proportion)
            themeColors.offer(themeColor)
        }

        return ArrayList(themeColors)
    }

    /**
     * @param bitmap   Image data [[A, R, G, B], ...]
     * @param maxColor Between [2, 256]
     * @param fraction Between [0.3, 0.9]
     * @param sigbits  5 or 6
     */
    /**
     * @param bitmap   Image data [[A, R, G, B], ...]
     * @param maxColor Between [2, 256]
     */
    init {
        val width = bitmap.width
        val height = bitmap.height
        require(!(width > 100 || height > 100)) { "width/height of bitmap should be less than 100!" }
        mWidth = width
        mHeight = height
        require(!(maxColor < 2 || maxColor > 256)) { "maxColor should be between [2, 256]!" }
        mMaxColor = maxColor
        require(!(fraction < 0.3 || fraction > 0.9)) { "fraction should be between [0.3, 0.9]!" }
        mFraction = fraction
        require(!(sigbits < 5 || sigbits > 6)) { "sigbits should be between [5, 6]!" }
        mSigbits = sigbits
        mRshift = 8 - mSigbits

        mPixelRGB = IntArray(width * height)
        bitmap.readPixels(mPixelRGB, 0, 0, width, height, 0, width)

        initPixHisto()
    }

    /**
     * The color space is divided up into a set of 3D rectangular regions (called `vboxes`)
     */
    private class VBox(
        val r1: Int,
        val r2: Int,
        val g1: Int,
        val g2: Int,
        val b1: Int,
        val b2: Int,
        val mMultiple: Int,
        val mHisto: MutableMap<Int, Int>
    ) : Comparable<VBox> {
        val mNumPixs: Long
        val mVolume: Long
        var mAxis: Int = 0
        private var mAvgColor = -1

        init {
            mNumPixs = population()
            val rl = (abs((r2 - r1).toDouble()) + 1).toInt()
            val gl = (abs((g2 - g1).toDouble()) + 1).toInt()
            val bl = (abs((b2 - b1).toDouble()) + 1).toInt()
            mVolume = rl.toLong() * gl * bl
            val max = max(max(rl.toDouble(), gl.toDouble()), bl.toDouble())
                .toInt()
            mAxis = if (max == rl) {
                COLOR_RED
            } else if (max == gl) {
                COLOR_GREEN
            } else {
                COLOR_BLUE
            }
        }

        private fun population(): Long {
            var sum: Long = 0
            for (r in r1..r2) {
                for (g in g1..g2) {
                    for (b in b1..b2) {
                        val count = mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                        sum += count
                    }
                }
            }
            return sum
        }

        val avgColor: Int
            get() {
                if (mAvgColor == -1) {
                    var total: Long = 0
                    var rSum: Long = 0
                    var gSum: Long = 0
                    var bSum: Long = 0

                    for (r in r1..r2) {
                        for (g in g1..g2) {
                            for (b in b1..b2) {
                                val count = mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                                if (count != 0) {
                                    total += count
                                    rSum = (rSum + count * (r + 0.5) * mMultiple).toLong()
                                    gSum = (gSum + count * (g + 0.5) * mMultiple).toLong()
                                    bSum = (bSum + count * (b + 0.5) * mMultiple).toLong()
                                }
                            }
                        }
                    }

                    val r: Int
                    val g: Int
                    val b: Int
                    if (total == 0L) {
                        r = (r1 + r2 + 1) * mMultiple / 2
                        g = (g1 + g2 + 1) * mMultiple / 2
                        b = (b2 + b2 + 1) * mMultiple / 2
                    } else {
                        r = (rSum / total).toInt()
                        g = (gSum / total).toInt()
                        b = (bSum / total).toInt()
                    }
                    mAvgColor = Color.rgb(r, g, b)
                }

                return mAvgColor
            }

        val priority: Long
            get() = -mNumPixs

        override fun compareTo(o: VBox): Int {
            return priority.compareTo(o.priority)
        }
    }

    class ThemeColor(val color: Int, val proportion: Double) : Comparable<ThemeColor> {
        private val mPriority: Double

        private var mGeneratedTextColors = false
        private var mIsDarkText = false
        private var mTitleTextColor = 0
        private var mBodyTextColor = 0

        init {
            //Log.d(TAG, "proportion:" + proportion + " RGB:" + Color.red(color) + " " + Color.green(color) + " " + Color.blue(color))
            val lab = DoubleArray(3)
            ColorUtils.colorToLAB(color, lab)
            val lightWeight = (1.0 - lab[0] / 100.0) * 3.0
            mPriority = proportion * (if (lightWeight <= 1.0) lightWeight else sqrt(lightWeight))
        }

        override fun compareTo(themeColor: ThemeColor): Int {
            return themeColor.mPriority.compareTo(mPriority)
        }

        val isDarkText: Boolean
            get() {
                ensureTextColorsGenerated()
                return mIsDarkText
            }

        val bodyTextColor: Int
            get() {
                ensureTextColorsGenerated()
                return mBodyTextColor
            }

        val titleTextColor: Int
            get() {
                ensureTextColorsGenerated()
                return mTitleTextColor
            }

        private fun ensureTextColorsGenerated() {
            if (!mGeneratedTextColors) {
                // First check white, as most colors will be dark
                val lightBodyAlpha = ColorUtils.calculateMinimumAlpha(
                    Color.WHITE, color, MIN_CONTRAST_BODY_TEXT
                )
                val lightTitleAlpha = ColorUtils.calculateMinimumAlpha(
                    Color.WHITE, color, MIN_CONTRAST_TITLE_TEXT
                )

                if (lightBodyAlpha != -1 && lightTitleAlpha != -1) {
                    // If we found valid light values, use them and return
                    mBodyTextColor = ColorUtils.setAlphaComponent(Color.WHITE, lightBodyAlpha)
                    mTitleTextColor = ColorUtils.setAlphaComponent(Color.WHITE, lightTitleAlpha)
                    mGeneratedTextColors = true
                    mIsDarkText = false
                    return
                }

                val darkBodyAlpha = ColorUtils.calculateMinimumAlpha(
                    Color.BLACK, color, MIN_CONTRAST_BODY_TEXT
                )
                val darkTitleAlpha = ColorUtils.calculateMinimumAlpha(
                    Color.BLACK, color, MIN_CONTRAST_TITLE_TEXT
                )

                if (darkBodyAlpha != -1 && darkTitleAlpha != -1) {
                    // If we found valid dark values, use them and return
                    mBodyTextColor = ColorUtils.setAlphaComponent(Color.BLACK, darkBodyAlpha)
                    mTitleTextColor = ColorUtils.setAlphaComponent(Color.BLACK, darkTitleAlpha)
                    mGeneratedTextColors = true
                    mIsDarkText = true
                    return
                }

                // If we reach here then we can not find title and body values which use the same
                // lightness, we need to use mismatched values
                mBodyTextColor = if (lightBodyAlpha != -1
                ) ColorUtils.setAlphaComponent(Color.WHITE, lightBodyAlpha)
                else ColorUtils.setAlphaComponent(Color.BLACK, darkBodyAlpha)
                mTitleTextColor = if (lightTitleAlpha != -1
                ) ColorUtils.setAlphaComponent(Color.WHITE, lightTitleAlpha)
                else ColorUtils.setAlphaComponent(Color.BLACK, darkTitleAlpha)
                mGeneratedTextColors = true
                mIsDarkText = lightTitleAlpha == -1
            }
        }

        companion object {
            private const val MIN_CONTRAST_TITLE_TEXT = 3.0f
            private const val MIN_CONTRAST_BODY_TEXT = 4.5f
        }
    }

    companion object {
        private val TAG: String = "MMCQ"

        private const val MAX_ITERATIONS = 100

        private const val COLOR_ALPHA = 0
        private const val COLOR_RED = 1
        private const val COLOR_GREEN = 2
        private const val COLOR_BLUE = 3

        fun getColorIndexWithRgb(red: Int, green: Int, blue: Int): Int {
            return (red shl 16) or (green shl 8) or blue
        }

        private fun medianCutApply(vBox: VBox): Array<VBox?> {
            var nPixs: Long = 0

            when (vBox.mAxis) {
                COLOR_RED -> {
                    var r = vBox.r1
                    while (r <= vBox.r2) {
                        var g = vBox.g1
                        while (g <= vBox.g2) {
                            var b = vBox.b1
                            while (b <= vBox.b2) {
                                val count = vBox.mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                                nPixs += count
                                b++
                            }
                            g++
                        }
                        if (nPixs >= vBox.mNumPixs / 2) {
                            val left = r - vBox.r1
                            val right = vBox.r2 - r
                            val r2 = if ((left >= right)) max(
                                vBox.r1.toDouble(), (r - 1 - left / 2).toDouble()
                            )
                                .toInt() else min(
                                (vBox.r2 - 1).toDouble(),
                                (r + right / 2).toDouble()
                            )
                                .toInt()
                            val vBox1 = VBox(
                                vBox.r1, r2, vBox.g1, vBox.g2, vBox.b1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            val vBox2 = VBox(
                                r2 + 1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            //Log.d(TAG, "VBOX " + vBox1.mNumPixs + " " + vBox2.mNumPixs);
                            if (isSimilarColor(vBox1.avgColor, vBox2.avgColor)) {
                                break
                            } else {
                                return arrayOf(vBox1, vBox2)
                            }
                        }
                        r++
                    }

                    var g = vBox.g1
                    while (g <= vBox.g2) {
                        var b = vBox.b1
                        while (b <= vBox.b2) {
                            var r = vBox.r1
                            while (r <= vBox.r2) {
                                val count = vBox.mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                                nPixs += count
                                r++
                            }
                            b++
                        }
                        if (nPixs >= vBox.mNumPixs / 2) {
                            val left = g - vBox.g1
                            val right = vBox.g2 - g
                            val g2 = if ((left >= right)) max(
                                vBox.g1.toDouble(), (g - 1 - left / 2).toDouble()
                            )
                                .toInt() else min(
                                (vBox.g2 - 1).toDouble(),
                                (g + right / 2).toDouble()
                            )
                                .toInt()
                            val vBox1 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, g2, vBox.b1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            val vBox2 = VBox(
                                vBox.r1, vBox.r2, g2 + 1, vBox.g2, vBox.b1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            //Log.d(TAG, "VBOX " + vBox1.mNumPixs + " " + vBox2.mNumPixs);
                            if (isSimilarColor(vBox1.avgColor, vBox2.avgColor)) {
                                break
                            } else {
                                return arrayOf(vBox1, vBox2)
                            }
                        }
                        g++
                    }

                    var b = vBox.b1
                    while (b <= vBox.b2) {
                        var r = vBox.r1
                        while (r <= vBox.r2) {
                            var g = vBox.g1
                            while (g <= vBox.g2) {
                                val count = vBox.mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                                nPixs += count
                                g++
                            }
                            r++
                        }
                        if (nPixs >= vBox.mNumPixs / 2) {
                            val left = b - vBox.b1
                            val right = vBox.b2 - b
                            val b2 = if ((left >= right)) max(
                                vBox.b1.toDouble(), (b - 1 - left / 2).toDouble()
                            )
                                .toInt() else min(
                                (vBox.b2 - 1).toDouble(),
                                (b + right / 2).toDouble()
                            )
                                .toInt()
                            val vBox1 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            val vBox2 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, vBox.g2, b2 + 1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            //Log.d(TAG, "VBOX " + vBox1.mNumPixs + " " + vBox2.mNumPixs);
                            if (isSimilarColor(vBox1.avgColor, vBox2.avgColor)) {
                                break
                            } else {
                                return arrayOf(vBox1, vBox2)
                            }
                        }
                        b++
                    }
                }

                COLOR_GREEN -> {
                    var g = vBox.g1
                    while (g <= vBox.g2) {
                        var b = vBox.b1
                        while (b <= vBox.b2) {
                            var r = vBox.r1
                            while (r <= vBox.r2) {
                                val count = vBox.mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                                nPixs += count
                                r++
                            }
                            b++
                        }
                        if (nPixs >= vBox.mNumPixs / 2) {
                            val left = g - vBox.g1
                            val right = vBox.g2 - g
                            val g2 = if ((left >= right)) max(
                                vBox.g1.toDouble(), (g - 1 - left / 2).toDouble()
                            )
                                .toInt() else min(
                                (vBox.g2 - 1).toDouble(),
                                (g + right / 2).toDouble()
                            )
                                .toInt()
                            val vBox1 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, g2, vBox.b1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            val vBox2 = VBox(
                                vBox.r1, vBox.r2, g2 + 1, vBox.g2, vBox.b1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            if (isSimilarColor(vBox1.avgColor, vBox2.avgColor)) {
                                break
                            } else {
                                return arrayOf(vBox1, vBox2)
                            }
                        }
                        g++
                    }

                    var b = vBox.b1
                    while (b <= vBox.b2) {
                        var r = vBox.r1
                        while (r <= vBox.r2) {
                            var g = vBox.g1
                            while (g <= vBox.g2) {
                                val count = vBox.mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                                nPixs += count
                                g++
                            }
                            r++
                        }
                        if (nPixs >= vBox.mNumPixs / 2) {
                            val left = b - vBox.b1
                            val right = vBox.b2 - b
                            val b2 = if ((left >= right)) max(
                                vBox.b1.toDouble(), (b - 1 - left / 2).toDouble()
                            )
                                .toInt() else min(
                                (vBox.b2 - 1).toDouble(),
                                (b + right / 2).toDouble()
                            )
                                .toInt()
                            val vBox1 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            val vBox2 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, vBox.g2, b2 + 1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            if (isSimilarColor(vBox1.avgColor, vBox2.avgColor)) {
                                break
                            } else {
                                return arrayOf(vBox1, vBox2)
                            }
                        }
                        b++
                    }
                }

                COLOR_BLUE -> {
                    var b = vBox.b1
                    while (b <= vBox.b2) {
                        var r = vBox.r1
                        while (r <= vBox.r2) {
                            var g = vBox.g1
                            while (g <= vBox.g2) {
                                val count = vBox.mHisto[getColorIndexWithRgb(r, g, b)] ?: 0
                                nPixs += count
                                g++
                            }
                            r++
                        }
                        if (nPixs >= vBox.mNumPixs / 2) {
                            val left = b - vBox.b1
                            val right = vBox.b2 - b
                            val b2 = if ((left >= right)) max(
                                vBox.b1.toDouble(), (b - 1 - left / 2).toDouble()
                            )
                                .toInt() else min(
                                (vBox.b2 - 1).toDouble(),
                                (b + right / 2).toDouble()
                            )
                                .toInt()
                            val vBox1 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, vBox.g2, vBox.b1, b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            val vBox2 = VBox(
                                vBox.r1, vBox.r2, vBox.g1, vBox.g2, b2 + 1, vBox.b2,
                                vBox.mMultiple, vBox.mHisto
                            )
                            if (isSimilarColor(vBox1.avgColor, vBox2.avgColor)) {
                                break
                            } else {
                                return arrayOf(vBox1, vBox2)
                            }
                        }
                        b++
                    }
                }
            }
            return arrayOf(vBox, null)
        }

        private fun iterCut(maxColor: Int, boxQueue: PriorityQueue<VBox>) {
            var nColors = 1
            var nIters = 0
            val store: MutableList<VBox> = ArrayList()
            while (true) {
                if (nColors >= maxColor) {
                    break
                }
                val vBox = boxQueue.poll() ?: break
                if (vBox.mNumPixs == 0L) {
                    //Log.w(TAG, "Vbox has no pixels")
                    //boxQueue.offer(vBox);
                    continue
                }
                val vBoxes = medianCutApply(vBox)
                if (vBoxes[0]!! === vBox || vBoxes[0]!!.mNumPixs == vBox.mNumPixs) {
                    store.add(vBoxes[0]!!)
                    continue
                }
                boxQueue.offer(vBoxes[0]!!)
                //if (vBoxes[1] != null) {
                nColors += 1
                boxQueue.offer(vBoxes[1]!!)
                //}
                nIters += 1
                if (nIters >= MAX_ITERATIONS) {
                    //Log.w(TAG, "Infinite loop; perhaps too few pixels!")
                    break
                }
            }
            boxQueue.addAll(store)
        }

        fun getColorPart(color: Int, @ColorPart which: Int): Int {
            return when (which) {
                COLOR_ALPHA -> Color.alpha(color)
                COLOR_RED -> Color.red(color)
                COLOR_GREEN -> Color.green(color)
                COLOR_BLUE -> Color.blue(color)
                else -> throw IllegalArgumentException(
                    "parameter which must be COLOR_ALPHA/COLOR_RED/COLOR_GREEN/COLOR_BLUE !"
                )
            }
        }

        private const val COLOR_TOLERANCE = 0.5

        fun isSimilarColor(color1: Int, color2: Int): Boolean {
            return colorDistance(color1, color2) < COLOR_TOLERANCE
        }

        fun colorDistance(color1: Int, color2: Int): Double {
            val r1 = Color.red(color1)
            val g1 = Color.green(color1)
            val b1 = Color.blue(color1)
            val r2 = Color.red(color2)
            val g2 = Color.green(color2)
            val b2 = Color.blue(color2)
            val rd = (r1 - r2) / 255.0
            val gd = (g1 - g2) / 255.0
            val bd = (b1 - b2) / 255.0
            return sqrt(rd * rd + gd * gd + bd * bd)
        }

        fun distanceToBGW(color: Int): Double {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            val rg = (r - g) / 255.0
            val gb = (g - b) / 255.0
            val br = (b - r) / 255.0
            return sqrt((rg * rg + gb * gb + br * br) / 3.0)
        }
    }
}
