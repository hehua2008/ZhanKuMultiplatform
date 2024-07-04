package com.hym.zhankucompose.util

import java.lang.ref.WeakReference

/**
 * @author hehua2008
 * @date 2024/7/4
 */
class WeakMap<K : Any, V : Any?> : MutableMap<K, V> {
    private val internalMap = mutableMapOf<WeakReference<K>, V>()

    private inner class MutableEntry(
        override val key: K,
        val weakEntry: MutableMap.MutableEntry<WeakReference<K>, V>
    ) : MutableMap.MutableEntry<K, V> {
        override val value: V
            get() = weakEntry.value

        override fun setValue(newValue: V): V {
            return weakEntry.setValue(newValue)
        }
    }

    private abstract inner class BaseIterator<E> : MutableIterator<E> {
        private val innerIterator = internalMap.entries.iterator()

        var holdKey: K? = null
        var holdNext: MutableMap.MutableEntry<WeakReference<K>, V>? = null

        override fun hasNext(): Boolean {
            holdKey = null
            holdNext = null
            while (true) {
                if (!innerIterator.hasNext()) {
                    return false
                } else {
                    val innerNext = innerIterator.next()
                    val key = innerNext.key.get()
                    if (key == null) {
                        innerIterator.remove()
                        continue
                    } else {
                        holdKey = key
                        holdNext = innerNext
                        return true
                    }
                }
            }
        }

        override fun remove() {
            holdKey = null
            holdNext = null
            innerIterator.remove()
        }
    }

    private inner class EntryIterator : BaseIterator<MutableMap.MutableEntry<K, V>>() {
        override fun next(): MutableMap.MutableEntry<K, V> {
            return MutableEntry(holdKey!!, holdNext!!).also {
                holdKey = null
                holdNext = null
            }
        }
    }

    private inner class KeyIterator : BaseIterator<K>() {
        override fun next(): K {
            return holdKey!!.also {
                holdKey = null
                holdNext = null
            }
        }
    }

    private inner class ValueIterator : BaseIterator<V>() {
        override fun next(): V {
            return holdNext!!.value.also {
                holdKey = null
                holdNext = null
            }
        }
    }

    private inner class EntrySet : AbstractMutableSet<MutableMap.MutableEntry<K, V>>() {
        override val size: Int
            get() = this@WeakMap.size

        override fun add(element: MutableMap.MutableEntry<K, V>): Boolean {
            throw UnsupportedOperationException()
        }

        override fun clear() {
            internalMap.clear()
        }

        override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
            return EntryIterator()
        }

        override fun contains(element: MutableMap.MutableEntry<K, V>): Boolean {
            var result = false
            iterateInternalMap { key, value ->
                if (key == element.key) {
                    result = (value == element.value)
                    false
                } else {
                    true
                }
            }
            return result
        }

        override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
            var result = false
            var continueAction = true
            val iterator = internalMap.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val key = next.key.get()
                if (key == null) {
                    iterator.remove()
                } else if (continueAction && key == element.key) {
                    if (next.value == element.value) {
                        result = true
                        iterator.remove()
                    }
                    continueAction = false
                } else {
                    // Keep iterating
                }
            }
            return result
        }
    }

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = EntrySet()

    private inner class KeySet : AbstractMutableSet<K>() {
        override val size: Int
            get() = this@WeakMap.size

        override fun add(element: K): Boolean {
            throw UnsupportedOperationException()
        }

        override fun clear() {
            internalMap.clear()
        }

        override fun iterator(): MutableIterator<K> {
            return KeyIterator()
        }

        override fun contains(element: K): Boolean {
            return this@WeakMap.containsKey(element)
        }

        override fun remove(element: K): Boolean {
            var result = false
            var continueAction = true
            val iterator = internalMap.iterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                val key = next.key.get()
                if (key == null) {
                    iterator.remove()
                } else if (continueAction && key == element) {
                    result = true
                    iterator.remove()
                    continueAction = false
                } else {
                    // Keep iterating
                }
            }
            return result
        }
    }

    override val keys: MutableSet<K>
        get() = KeySet()

    override val values: MutableCollection<V>
        get() {
            val valueList = mutableListOf<V>()
            iterateInternalMap { key, value ->
                val ignore = valueList.add(value)
                true
            }
            return valueList
        }

    override val size: Int
        get() {
            var count = 0
            iterateInternalMap { key, value ->
                count += 1
                true
            }
            return count
        }

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun clear() {
        internalMap.clear()
    }

    override fun put(key: K, value: V): V? {
        var result: V? = null
        var valueSet = false
        iterateInternalMap { entry ->
            if (key == entry.key) {
                result = entry.value
                entry.setValue(value)
                valueSet = true
                false
            } else {
                true
            }
        }
        if (!valueSet) {
            internalMap[WeakReference(key)] = value
        }
        return result
    }

    override fun putAll(from: Map<out K, V>) {
        val mutableFrom = from.toMutableMap()
        iterateInternalMap { entry ->
            val newValue = mutableFrom.remove(entry.key)
            if (newValue != null || /* not mutableFrom! */from.containsKey(entry.key)) {
                entry.setValue(newValue as V)
            }
            true
        }
        for ((key, value) in mutableFrom.entries) {
            internalMap[WeakReference(key)] = value
        }
    }

    override fun get(key: K): V? {
        var result: V? = null
        iterateInternalMap { k, v ->
            if (key == k) {
                result = v
                false
            } else {
                true
            }
        }
        return result
    }

    override fun containsKey(key: K): Boolean {
        var result = false
        iterateInternalMap { k, v ->
            if (key == k) {
                result = true
                false
            } else {
                true
            }
        }
        return result
    }

    override fun containsValue(value: V): Boolean {
        var result = false
        iterateInternalMap { k, v ->
            if (value == v) {
                result = true
                false
            } else {
                true
            }
        }
        return result
    }

    override fun remove(key: K): V? {
        var result: V? = null
        var continueAction = true
        val iterator = internalMap.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val k = next.key.get()
            if (k == null) {
                iterator.remove()
            } else if (continueAction && k == key) {
                result = next.value
                iterator.remove()
                continueAction = false
            } else {
                // Keep iterating
            }
        }
        return result
    }

    private fun iterateInternalMap(action: (key: K, value: V) -> Boolean) {
        var continueAction = true
        val iterator = internalMap.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val key = next.key.get()
            if (key == null) {
                iterator.remove()
            } else if (continueAction) {
                continueAction = action(key, next.value)
            } else {
                // Keep iterating
            }
        }
    }

    private fun iterateInternalMap(action: (entry: MutableEntry) -> Boolean) {
        var continueAction = true
        val iterator = internalMap.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val key = next.key.get()
            if (key == null) {
                iterator.remove()
            } else if (continueAction) {
                continueAction = action(MutableEntry(key, next))
            } else {
                // Keep iterating
            }
        }
    }
}
