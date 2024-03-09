package com.hym.zhankucompose.compose

import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.MotionDurationScale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * @author hehua2008
 * @date 2024/3/9
 */
private val ListenableScrollMotionDurationScale = object : MotionDurationScale {
    override val scaleFactor: Float = 1f
}

class ListenableFlingBehavior(
    private val flingVelocityListener: FlingVelocityListener,
    private val flingDecay: DecayAnimationSpec<Float>,
    private val motionDurationScale: MotionDurationScale = ListenableScrollMotionDurationScale
) : FlingBehavior {

    // For Testing
    var lastAnimationCycleCount = 0

    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        lastAnimationCycleCount = 0
        // come up with the better threshold, but we need it since spline curve gives us NaNs
        return withContext(motionDurationScale) {
            if (abs(initialVelocity) > 1f) {
                var velocityLeft = initialVelocity
                var lastValue = 0f
                val animationState = AnimationState(
                    initialValue = 0f,
                    initialVelocity = initialVelocity,
                )
                try {
                    flingVelocityListener.onStartFling(initialVelocity)
                    animationState.animateDecay(flingDecay) {
                        val delta = value - lastValue
                        val consumed = scrollBy(delta)
                        lastValue = value
                        velocityLeft = this.velocity
                        flingVelocityListener.onFlingVelocityDecayed(velocityLeft)
                        // avoid rounding errors and stop if anything is unconsumed
                        if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
                        lastAnimationCycleCount++
                    }
                } catch (exception: CancellationException) {
                    velocityLeft = animationState.velocity
                } finally {
                    flingVelocityListener.onEndFling()
                }
                velocityLeft
            } else {
                initialVelocity
            }
        }
    }
}

interface FlingVelocityListener {
    fun onStartFling(initialVelocity: Float)

    fun onFlingVelocityDecayed(remainingVelocity: Float)

    fun onEndFling()
}

/**
 * Creates a [ListenableFlingBehavior] that is remembered across compositions.
 *
 * @param flingVelocityListener the listener for fling velocity
 */
@Composable
fun listenableFlingBehavior(flingVelocityListener: FlingVelocityListener): FlingBehavior {
    val flingSpec = rememberSplineBasedDecay<Float>()
    return remember(flingVelocityListener, flingSpec) {
        ListenableFlingBehavior(flingVelocityListener, flingSpec)
    }
}
