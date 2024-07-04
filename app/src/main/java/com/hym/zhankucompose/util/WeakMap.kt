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

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val result = mutableSetOf<MutableMap.MutableEntry<K, V>>()
            val weakEntries = internalMap.entries
            for (weakEntry in weakEntries) {
                val key = weakEntry.key.get() ?: continue
                result.add(MutableEntry(key, weakEntry))
            }
            return result
        }

    override val keys: MutableSet<K>
        get() {
            val keySet = mutableSetOf<K>()
            iterateInternalMap { key, value ->
                val ignore = keySet.add(key)
                true
            }
            return keySet
        }

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
