package com.hym.logcollector.util

/**
 * @author hehua2008
 * @date 2021/8/20
 */
open class RingSet<E>(capacity: Int) : MixinListSet<E>(capacity) {
    override fun add(element: E): Boolean {
        if (!mSet.add(element)) return false
        if (mSet.size == capacity + 1) {
            mSet.iterator().let {
                it.next()
                it.remove()
            }
        }
        return true
    }

    override fun addAll(elements: Collection<E>): Boolean {
        var modified = false
        elements.forEach {
            if (add(it)) modified = true
        }
        return modified
    }

    fun isFull(): Boolean = mSet.size == capacity
}