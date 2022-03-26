package com.hym.logcollector.util

/**
 * @author hehua2008
 * @date 2021/8/17
 */
open class RingList<E>(val capacity: Int) : AbstractMutableList<E>() {
    private val mElements by lazy { arrayOfNulls<Any?>(capacity) }
    private var mOffset = 0

    final override var size: Int = 0
        private set

    private fun increaseOffset(step: Int = 1) {
        mOffset += step
        if (mOffset >= capacity) mOffset %= capacity
    }

    override fun removeAt(index: Int): E {
        checkIndex(index)
        val element = getInline(index)
        if (index >= size - 1 - index) {
            shiftToLeft(index, size - 1)
        } else {
            shiftToRight(0, index)
            increaseOffset()
        }
        size--
        return element
    }

    @Deprecated("UnsupportedOperation", ReplaceWith("add(element)"))
    override fun add(index: Int, element: E) {
        throw UnsupportedOperationException()
    }

    override fun add(element: E): Boolean {
        if (isFull()) removeAt(0)
        setInline(size++, element)
        return true
    }

    @Deprecated("UnsupportedOperation", ReplaceWith("addAll(elements)"))
    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addAll(elements: Collection<E>): Boolean {
        val srcSize = elements.size
        if (srcSize == 0) return false
        if (srcSize >= capacity) {
            val skipSize = srcSize - capacity
            elements.forEachIndexed { index, e ->
                if (index >= skipSize) mElements[index - skipSize] = e
            }
            mOffset = 0
            size = capacity
        } else {
            val removeSize = size + srcSize - capacity
            if (removeSize > 0) {
                for (idx in 0 until removeSize) {
                    setInline(idx, null)
                }
                increaseOffset(removeSize)
                size -= removeSize
            }
            elements.forEach {
                add(it)
            }
        }
        return true
    }

    override fun set(index: Int, element: E): E {
        checkIndex(index)
        return setInline(index, element) as E
    }

    override fun get(index: Int): E {
        checkIndex(index)
        return getInline(index)
    }

    fun isFull(): Boolean = size == capacity

    private fun shiftToLeft(startIndex: Int, endIndex: Int, step: Int = 1) {
        for (idx in startIndex..(endIndex - step)) {
            val newPos = getRealIndex(idx)
            val oldPos = getRealIndex(idx + step)
            mElements[newPos] = mElements[oldPos]
            if (idx + step > endIndex - step) {
                mElements[oldPos] = null
            }
        }
    }

    private fun shiftToRight(startIndex: Int, endIndex: Int, step: Int = 1) {
        for (idx in endIndex downTo (startIndex + step)) {
            val newPos = getRealIndex(idx)
            val oldPos = getRealIndex(idx - step)
            mElements[newPos] = mElements[oldPos]
            if (idx - step < startIndex + step) {
                mElements[oldPos] = null
            }
        }
    }

    private fun setInline(index: Int, element: E?): E? {
        val realIndex = getRealIndex(index)
        val oldElement = mElements[realIndex] as E?
        mElements[realIndex] = element
        return oldElement
    }

    private fun getInline(index: Int): E {
        val realIndex = getRealIndex(index)
        return mElements[realIndex] as E
    }

    private fun getRealIndex(index: Int): Int {
        return (mOffset + index) % capacity
    }

    private inline fun checkIndex(index: Int) {
        if (index < 0 || index >= size) throw IndexOutOfBoundsException(outOfBoundsMsg(index))
    }

    private inline fun outOfBoundsMsg(index: Int): String {
        return "Index: $index, Size: $size"
    }
}