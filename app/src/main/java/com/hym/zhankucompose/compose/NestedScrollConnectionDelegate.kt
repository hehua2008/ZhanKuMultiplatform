package com.hym.zhankucompose.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

/**
 * @author hehua2008
 * @date 2024/3/9
 */
abstract class NestedScrollConnectionDelegate(
    private val delegated: NestedScrollConnection
) : NestedScrollConnection {
    final override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        //beforePreScroll(available, source)
        val result = delegated.onPreScroll(available, source)
        afterPreScroll(available, source, result)
        return result
    }

    final override fun onPostScroll(
        consumed: Offset, available: Offset, source: NestedScrollSource
    ): Offset {
        //beforePostScroll(consumed, available, source)
        val result = delegated.onPostScroll(consumed, available, source)
        afterPostScroll(consumed, available, source, result)
        return result
    }

    final override suspend fun onPreFling(available: Velocity): Velocity {
        //beforePreFling(available)
        val result = delegated.onPreFling(available)
        afterPreFling(available, result)
        return result
    }

    final override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        //beforePostFling(consumed, available)
        val result = delegated.onPostFling(consumed, available)
        afterPostFling(consumed, available, result)
        return result
    }

    //open fun beforePreScroll(available: Offset, source: NestedScrollSource) {}

    open fun afterPreScroll(available: Offset, source: NestedScrollSource, selfConsumed: Offset) {}

    //open fun beforePostScroll(consumed: Offset, available: Offset, source: NestedScrollSource) {}

    open fun afterPostScroll(
        consumed: Offset, available: Offset, source: NestedScrollSource, selfConsumed: Offset
    ) {
    }

    //open fun beforePreFling(available: Velocity) {}

    open fun afterPreFling(available: Velocity, selfConsumed: Velocity) {}

    //open fun beforePostFling(consumed: Velocity, available: Velocity) {}

    open fun afterPostFling(consumed: Velocity, available: Velocity, selfConsumed: Velocity) {}

    final override fun equals(other: Any?): Boolean {
        other ?: return false
        if (this === other) return true
        if (this::class != other::class) return false
        return delegated == (other as NestedScrollConnectionDelegate).delegated
    }

    final override fun hashCode(): Int {
        return delegated.hashCode()
    }

    final override fun toString(): String {
        return "${this::class.simpleName}[$delegated]"
    }
}
