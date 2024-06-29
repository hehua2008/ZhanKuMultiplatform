package com.hym.zhankucompose.flow

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

/**
 * @author hehua2008
 * @date 2024/6/28
 */
interface SharedData<out T> : SharedFlow<T> {
    /**
     * The current value of this state flow.
     */
    val value: T
}

interface MutableSharedData<T> : SharedData<T>, MutableSharedFlow<T> {
    /**
     * The current value of this state flow.
     *
     * Setting a value that is [equal][Any.equals] to the previous one does nothing.
     *
     * This property is **thread-safe** and can be safely updated from concurrent coroutines without
     * external synchronization.
     */
    override var value: T
}

private class MutableSharedDataImpl<T>(
    val shared: MutableSharedFlow<T>
) : MutableSharedData<T>, MutableSharedFlow<T> by shared {
    constructor(initialValue: T) : this(
        MutableSharedFlow<T>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        ).apply {
            tryEmit(initialValue)
        })

    override var value: T
        get() = shared.replayCache.last()
        set(value) {
            shared.tryEmit(value)
        }
}

fun <T> MutableSharedData(initialValue: T): MutableSharedData<T> {
    return MutableSharedDataImpl(initialValue)
}
