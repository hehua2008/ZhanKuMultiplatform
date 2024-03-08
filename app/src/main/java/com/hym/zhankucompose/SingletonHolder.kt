package com.hym.zhankucompose

/**
 * @author hehua2008
 * @date 2024/2/29
 */
open class SingletonHolder<out T : Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator

    @Volatile
    private var instance: T? = null

    fun getInstance(arg: A): T {
        val tmp = instance
        if (tmp != null) {
            return tmp
        }

        return synchronized(this) {
            var tmp2 = instance
            if (tmp2 == null) {
                tmp2 = creator!!(arg)
                instance = tmp2
                creator = null
            }
            tmp2
        }
    }
}

class SingletonDemo private constructor(obj: Any) {
    init {
        // Init using context argument
    }

    companion object : SingletonHolder<SingletonDemo, Any>(::SingletonDemo)
}
