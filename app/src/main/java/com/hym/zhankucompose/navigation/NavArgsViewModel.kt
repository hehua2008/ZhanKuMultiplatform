package com.hym.zhankucompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import com.hym.zhankucompose.util.WeakMap
import kotlin.reflect.KClass

/**
 * @author hehua2008
 * @date 2024/7/4
 */
class NavArgsViewModel : ViewModel() {
    val navigatingArgs = mutableMapOf<KClass<out NavArgs>, NavArgs>()

    val argumentsMap = WeakMap<String, NavArgs>()

    inline fun <reified T : NavArgs> putArgs(args: T) {
        navigatingArgs[T::class] = args
    }

    override fun onCleared() {
        navigatingArgs.clear()
        argumentsMap.clear()
    }
}

@Composable
inline fun <reified T : NavArgs> NavArgsViewModel.getArgs(backStackEntryId: String): T {
    return remember(this, backStackEntryId) {
        var args = argumentsMap[backStackEntryId] as T?
        if (args == null) {
            args = navigatingArgs.remove(T::class) as T
            argumentsMap[backStackEntryId] = args
        }
        args
    }
}
