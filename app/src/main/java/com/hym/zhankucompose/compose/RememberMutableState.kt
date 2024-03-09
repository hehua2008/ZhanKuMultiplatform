package com.hym.zhankucompose.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * @author hehua2008
 * @date 2024/3/9
 */
// region Object
/**
 * Creates a [MutableState] that is remembered across compositions.
 *
 * Changes to the provided [policy] argument will **not** result in the state being recreated or
 * changed in any way if it has already been created.
 */
@Composable
fun <T> rememberMutableState(
    key1: Any?,
    policy: SnapshotMutationPolicy<T?>? = null,
    calculation: @DisallowComposableCalls () -> T
): MutableState<T> {
    val mutableState = remember {
        if (policy == null) mutableStateOf<T?>(null)
        else mutableStateOf<T?>(null, policy)
    }
    remember(key1) {
        mutableState.value = calculation()
    }
    return mutableState as MutableState<T>
}

@Composable
fun <T> rememberMutableState(
    key1: Any?,
    key2: Any?,
    policy: SnapshotMutationPolicy<T?>? = null,
    calculation: @DisallowComposableCalls () -> T
): MutableState<T> {
    val mutableState = remember {
        if (policy == null) mutableStateOf<T?>(null)
        else mutableStateOf<T?>(null, policy)
    }
    remember(key1, key2) {
        mutableState.value = calculation()
    }
    return mutableState as MutableState<T>
}

@Composable
fun <T> rememberMutableState(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    policy: SnapshotMutationPolicy<T?>? = null,
    calculation: @DisallowComposableCalls () -> T
): MutableState<T> {
    val mutableState = remember {
        if (policy == null) mutableStateOf<T?>(null)
        else mutableStateOf<T?>(null, policy)
    }
    remember(key1, key2, key3) {
        mutableState.value = calculation()
    }
    return mutableState as MutableState<T>
}

@Composable
fun <T> rememberMutableState(
    vararg keys: Any?,
    policy: SnapshotMutationPolicy<T?>? = null,
    calculation: @DisallowComposableCalls () -> T
): MutableState<T> {
    val mutableState = remember {
        if (policy == null) mutableStateOf<T?>(null)
        else mutableStateOf<T?>(null, policy)
    }
    remember(*keys) {
        mutableState.value = calculation()
    }
    return mutableState as MutableState<T>
}
// endregion

// region Int
/**
 * Creates a [MutableIntState] that is remembered across compositions.
 */
@Composable
fun rememberMutableIntState(
    key1: Any?,
    calculation: @DisallowComposableCalls () -> Int
): MutableIntState {
    val mutableState = remember {
        mutableIntStateOf(0)
    }
    remember(key1) {
        mutableState.intValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableIntState(
    key1: Any?,
    key2: Any?,
    calculation: @DisallowComposableCalls () -> Int
): MutableIntState {
    val mutableState = remember {
        mutableIntStateOf(0)
    }
    remember(key1, key2) {
        mutableState.intValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableIntState(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    calculation: @DisallowComposableCalls () -> Int
): MutableIntState {
    val mutableState = remember {
        mutableIntStateOf(0)
    }
    remember(key1, key2, key3) {
        mutableState.intValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableIntState(
    vararg keys: Any?,
    calculation: @DisallowComposableCalls () -> Int
): MutableIntState {
    val mutableState = remember {
        mutableIntStateOf(0)
    }
    remember(*keys) {
        mutableState.intValue = calculation()
    }
    return mutableState
}
// endregion

// region Long
/**
 * Creates a [MutableLongState] that is remembered across compositions.
 */
@Composable
fun rememberMutableLongState(
    key1: Any?,
    calculation: @DisallowComposableCalls () -> Long
): MutableLongState {
    val mutableState = remember {
        mutableLongStateOf(0L)
    }
    remember(key1) {
        mutableState.longValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableLongState(
    key1: Any?,
    key2: Any?,
    calculation: @DisallowComposableCalls () -> Long
): MutableLongState {
    val mutableState = remember {
        mutableLongStateOf(0L)
    }
    remember(key1, key2) {
        mutableState.longValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableLongState(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    calculation: @DisallowComposableCalls () -> Long
): MutableLongState {
    val mutableState = remember {
        mutableLongStateOf(0L)
    }
    remember(key1, key2, key3) {
        mutableState.longValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableLongState(
    vararg keys: Any?,
    calculation: @DisallowComposableCalls () -> Long
): MutableLongState {
    val mutableState = remember {
        mutableLongStateOf(0L)
    }
    remember(*keys) {
        mutableState.longValue = calculation()
    }
    return mutableState
}
// endregion

// region Float
/**
 * Creates a [MutableFloatState] that is remembered across compositions.
 */
@Composable
fun rememberMutableFloatState(
    key1: Any?,
    calculation: @DisallowComposableCalls () -> Float
): MutableFloatState {
    val mutableState = remember {
        mutableFloatStateOf(0f)
    }
    remember(key1) {
        mutableState.floatValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableFloatState(
    key1: Any?,
    key2: Any?,
    calculation: @DisallowComposableCalls () -> Float
): MutableFloatState {
    val mutableState = remember {
        mutableFloatStateOf(0f)
    }
    remember(key1, key2) {
        mutableState.floatValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableFloatState(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    calculation: @DisallowComposableCalls () -> Float
): MutableFloatState {
    val mutableState = remember {
        mutableFloatStateOf(0f)
    }
    remember(key1, key2, key3) {
        mutableState.floatValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableFloatState(
    vararg keys: Any?,
    calculation: @DisallowComposableCalls () -> Float
): MutableFloatState {
    val mutableState = remember {
        mutableFloatStateOf(0f)
    }
    remember(*keys) {
        mutableState.floatValue = calculation()
    }
    return mutableState
}
// endregion

// region Double
/**
 * Creates a [MutableDoubleState] that is remembered across compositions.
 */
@Composable
fun rememberMutableDoubleState(
    key1: Any?,
    calculation: @DisallowComposableCalls () -> Double
): MutableDoubleState {
    val mutableState = remember {
        mutableDoubleStateOf(0.0)
    }
    remember(key1) {
        mutableState.doubleValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableDoubleState(
    key1: Any?,
    key2: Any?,
    calculation: @DisallowComposableCalls () -> Double
): MutableDoubleState {
    val mutableState = remember {
        mutableDoubleStateOf(0.0)
    }
    remember(key1, key2) {
        mutableState.doubleValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableDoubleState(
    key1: Any?,
    key2: Any?,
    key3: Any?,
    calculation: @DisallowComposableCalls () -> Double
): MutableDoubleState {
    val mutableState = remember {
        mutableDoubleStateOf(0.0)
    }
    remember(key1, key2, key3) {
        mutableState.doubleValue = calculation()
    }
    return mutableState
}

@Composable
fun rememberMutableDoubleState(
    vararg keys: Any?,
    calculation: @DisallowComposableCalls () -> Double
): MutableDoubleState {
    val mutableState = remember {
        mutableDoubleStateOf(0.0)
    }
    remember(*keys) {
        mutableState.doubleValue = calculation()
    }
    return mutableState
}
// endregion
