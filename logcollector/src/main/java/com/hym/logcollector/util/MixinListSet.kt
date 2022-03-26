package com.hym.logcollector.util

import java.util.*

/**
 * @author hehua2008
 * @date 2021/8/20
 */
open class MixinListSet<E> private constructor(
    val capacity: Int,
    protected val mSet: MutableSet<E>
) : AbstractList<E>(), MutableSet<E> by mSet {
    constructor(capacity: Int) : this(capacity, LinkedHashSet(capacity))

    override fun get(index: Int): E {
        checkIndex(index)
        mSet.iterator().let {
            repeat(index) { _ ->
                it.next()
            }
            return it.next()
        }
    }

    override fun removeAt(index: Int): E {
        checkIndex(index)
        mSet.iterator().let {
            repeat(index) { _ ->
                it.next()
            }
            val e = it.next()
            it.remove()
            return e
        }
    }

    override fun spliterator(): Spliterator<E> {
        return super<MutableSet>.spliterator()
    }

    private inline fun checkIndex(index: Int) {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException(outOfBoundsMsg(index))
    }

    private inline fun outOfBoundsMsg(index: Int): String {
        return "Index: $index, Size: $mSet.size"
    }
}