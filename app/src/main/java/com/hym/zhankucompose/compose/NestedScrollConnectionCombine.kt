package com.hym.zhankucompose.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 * @author hehua2008
 * @date 2024/3/9
 */
data class NestedScrollConnectionCombine(
    private val first: NestedScrollConnection, private val second: NestedScrollConnection
) : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val firstConsumed = first.onPreScroll(available, source)
        val secondConsumed = second.onPreScroll(available - firstConsumed, source)
        return firstConsumed + secondConsumed
    }

    override fun onPostScroll(
        consumed: Offset, available: Offset, source: NestedScrollSource
    ): Offset {
        val firstConsumed = first.onPostScroll(consumed, available, source)
        val secondConsumed =
            second.onPostScroll(consumed + firstConsumed, available - firstConsumed, source)
        return firstConsumed + secondConsumed
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        val firstConsumed = first.onPreFling(available)
        val secondConsumed = second.onPreFling(available - firstConsumed)
        return firstConsumed + secondConsumed
    }

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        val firstConsumed = first.onPostFling(consumed, available)
        val secondConsumed = second.onPostFling(consumed + firstConsumed, available - firstConsumed)
        return firstConsumed + secondConsumed
    }
}

operator fun NestedScrollConnection.plus(other: NestedScrollConnection): NestedScrollConnection =
    NestedScrollConnectionCombine(this, other)
