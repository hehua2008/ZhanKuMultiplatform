package com.hym.zhankucompose.util

import kotlin.math.max

/**
 * @author hehua2008
 * @date 2024/7/5
 */
class PriorityQueue<E : Any> : AbstractQueue<E> {
    /**
     * Priority queue represented as a balanced binary heap: the two
     * children of queue[n] are queue[2*n+1] and queue[2*(n+1)].  The
     * priority queue is ordered by comparator, or by the elements'
     * natural ordering, if comparator is null: For each node n in the
     * heap and each descendant d of n, n <= d.  The element with the
     * lowest value is in queue[0], assuming the queue is nonempty.
     */
    lateinit var queue: Array<E?> // non-private to simplify nested class access

    /**
     * The number of elements in the priority queue.
     */
    override var size: Int = 0
        private set

    /**
     * The comparator, or null if priority queue uses elements'
     * natural ordering.
     */
    private val comparator: Comparator<in E>?

    /**
     * The number of times this priority queue has been
     * *structurally modified*.  See AbstractList for gory details.
     */
    var modCount: Int = 0 // non-private to simplify nested class access

    /**
     * Creates a `PriorityQueue` with the default initial capacity and
     * whose elements are ordered according to the specified comparator.
     *
     * @param  comparator the comparator that will be used to order this
     * priority queue.  If `null`, the [         natural ordering][Comparable] of the elements will be used.
     * @since 1.8
     */
    constructor(comparator: Comparator<in E>?) : this(DEFAULT_INITIAL_CAPACITY, comparator)

    /**
     * Creates a `PriorityQueue` with the specified initial capacity
     * that orders its elements according to the specified comparator.
     *
     * @param  initialCapacity the initial capacity for this priority queue
     * @param  comparator the comparator that will be used to order this
     * priority queue.  If `null`, the [         natural ordering][Comparable] of the elements will be used.
     * @throws IllegalArgumentException if `initialCapacity` is
     * less than 1
     */
    /**
     * Creates a `PriorityQueue` with the default initial
     * capacity (11) that orders its elements according to their
     * [natural ordering][Comparable].
     */
    /**
     * Creates a `PriorityQueue` with the specified initial
     * capacity that orders its elements according to their
     * [natural ordering][Comparable].
     *
     * @param initialCapacity the initial capacity for this priority queue
     * @throws IllegalArgumentException if `initialCapacity` is less
     * than 1
     */
    constructor(
        initialCapacity: Int = DEFAULT_INITIAL_CAPACITY,
        comparator: Comparator<in E>? = null
    ) {
        // Note: This restriction of at least one is not actually needed,
        // but continues for 1.5 compatibility
        require(initialCapacity >= 1)
        this.queue = arrayOfNulls<Any>(initialCapacity) as Array<E?>
        this.comparator = comparator
    }

    /**
     * Creates a `PriorityQueue` containing the elements in the
     * specified collection.  If the specified collection is an instance of
     * a [SortedSet] or is another `PriorityQueue`, this
     * priority queue will be ordered according to the same ordering.
     * Otherwise, this priority queue will be ordered according to the
     * [natural ordering][Comparable] of its elements.
     *
     * @param  c the collection whose elements are to be placed
     * into this priority queue
     * @throws ClassCastException if elements of the specified collection
     * cannot be compared to one another according to the priority
     * queue's ordering
     * @throws NullPointerException if the specified collection or any
     * of its elements are null
     */
    constructor(c: Collection<E>) {
        when (c) {
            /*
            is SortedSet<*> -> {
                val ss = c as SortedSet<out E>
                this.comparator = ss.comparator() as Comparator<in E>?
                initElementsFromCollection(ss)
            }
             */

            is PriorityQueue<*> -> {
                val pq = c as PriorityQueue<out E>
                this.comparator = pq.comparator() as Comparator<in E>?
                initFromPriorityQueue(pq)
            }

            else -> {
                this.comparator = null
                initFromCollection(c)
            }
        }
    }

    /**
     * Creates a `PriorityQueue` containing the elements in the
     * specified priority queue.  This priority queue will be
     * ordered according to the same ordering as the given priority
     * queue.
     *
     * @param  c the priority queue whose elements are to be placed
     * into this priority queue
     * @throws ClassCastException if elements of `c` cannot be
     * compared to one another according to `c`'s
     * ordering
     * @throws NullPointerException if the specified priority queue or any
     * of its elements are null
     */
    constructor(c: PriorityQueue<out E>) {
        this.comparator = c.comparator() as Comparator<in E>?
        initFromPriorityQueue(c)
    }

    /**
     * Creates a `PriorityQueue` containing the elements in the
     * specified sorted set.   This priority queue will be ordered
     * according to the same ordering as the given sorted set.
     *
     * @param  c the sorted set whose elements are to be placed
     * into this priority queue
     * @throws ClassCastException if elements of the specified sorted
     * set cannot be compared to one another according to the
     * sorted set's ordering
     * @throws NullPointerException if the specified sorted set or any
     * of its elements are null
     */
    /*
    constructor(c: SortedSet<out E>) {
        this.comparator = c.comparator() as Comparator<in E>?
        initElementsFromCollection(c)
    }
     */

    private fun initFromPriorityQueue(c: PriorityQueue<out E>) {
        if (c::class == PriorityQueue::class) {
            this.queue = ensureNonEmpty(c.toTypedArray()) as Array<E?>
            this.size = c.size
        } else {
            initFromCollection(c)
        }
    }

    private fun initElementsFromCollection(c: Collection<E>) {
        var es: Array<Any?> = c.toTypedArray()
        val len = es.size
        es = es.copyOf(len)
        if (len == 1 || this.comparator != null) {
            for (e in es) {
                if (e == null) {
                    throw NullPointerException()
                }
            }
        }
        this.queue = ensureNonEmpty(es) as Array<E?>
        this.size = len
    }

    /**
     * Initializes queue array with elements from the given Collection.
     *
     * @param c the collection
     */
    private fun initFromCollection(c: Collection<E>) {
        initElementsFromCollection(c)
        heapify()
    }

    /**
     * Increases the capacity of the array.
     *
     * @param minCapacity the desired minimum capacity
     */
    private fun grow(minCapacity: Int) {
        val oldCapacity = queue.size
        // Double size if small; else grow by 50%
        val newCapacity = newLength(
            oldCapacity,
            minCapacity - oldCapacity,  /* minimum growth */
            if (oldCapacity < 64) oldCapacity + 2 else oldCapacity shr 1 /* preferred growth */
        )
        queue = queue.copyOf(newCapacity)
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return `true` (as specified by [Collection.add])
     * @throws ClassCastException if the specified element cannot be
     * compared with elements currently in this priority queue
     * according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    override fun add(e: E): Boolean {
        return offer(e)
    }

    /**
     * Inserts the specified element into this priority queue.
     *
     * @return `true` (as specified by [Queue.offer])
     * @throws ClassCastException if the specified element cannot be
     * compared with elements currently in this priority queue
     * according to the priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    override fun offer(e: E): Boolean {
        modCount++
        val i = size
        if (i >= queue.size) {
            grow(i + 1)
        }
        siftUp(i, e)
        size = i + 1
        return true
    }

    override fun peek(): E? {
        return queue[0] as E?
    }

    private fun indexOf(o: Any?): Int {
        if (o != null) {
            val es = queue
            var i = 0
            val n = size
            while (i < n) {
                if (o == es[i]) {
                    return i
                }
                i++
            }
        }
        return -1
    }

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally, removes an element `e` such
     * that `o.equals(e)`, if this queue contains one or more such
     * elements.  Returns `true` if and only if this queue contained
     * the specified element (or equivalently, if this queue changed as a
     * result of the call).
     *
     * @param o element to be removed from this queue, if present
     * @return `true` if this queue changed as a result of the call
     */
    override fun remove(o: E): Boolean {
        val i = indexOf(o)
        if (i == -1) {
            return false
        } else {
            removeAt(i)
            return true
        }
    }

    /**
     * Identity-based version for use in Itr.remove.
     *
     * @param o element to be removed from this queue, if present
     */
    fun removeEq(o: Any) {
        val es = queue
        var i = 0
        val n = size
        while (i < n) {
            if (o === es[i]) {
                removeAt(i)
                break
            }
            i++
        }
    }

    /**
     * Returns `true` if this queue contains the specified element.
     * More formally, returns `true` if and only if this queue contains
     * at least one element `e` such that `o.equals(e)`.
     *
     * @param o object to be checked for containment in this queue
     * @return `true` if this queue contains the specified element
     */
    override fun contains(o: E): Boolean {
        return indexOf(o) >= 0
    }

    /**
     * Returns an array containing all of the elements in this queue.
     * The elements are in no particular order.
     *
     *
     * The returned array will be "safe" in that no references to it are
     * maintained by this queue.  (In other words, this method must allocate
     * a new array).  The caller is thus free to modify the returned array.
     *
     *
     * This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this queue
     */
    override fun toArray(): Array<Any?> {
        return queue.copyOf(size) as Array<Any?>
    }

    /**
     * Returns an array containing all of the elements in this queue; the
     * runtime type of the returned array is that of the specified array.
     * The returned array elements are in no particular order.
     * If the queue fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this queue.
     *
     *
     * If the queue fits in the specified array with room to spare
     * (i.e., the array has more elements than the queue), the element in
     * the array immediately following the end of the collection is set to
     * `null`.
     *
     *
     * Like the [.toArray] method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     *
     *
     * Suppose `x` is a queue known to contain only strings.
     * The following code can be used to dump the queue into a newly
     * allocated array of `String`:
     *
     * <pre> `String[] y = x.toArray(new String[0]);`</pre>
     *
     * Note that `toArray(new Object[0])` is identical in function to
     * `toArray()`.
     *
     * @param a the array into which the elements of the queue are to
     * be stored, if it is big enough; otherwise, a new array of the
     * same runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this queue
     * @throws ArrayStoreException if the runtime type of the specified array
     * is not a supertype of the runtime type of every element in
     * this queue
     * @throws NullPointerException if the specified array is null
     */
    override fun <T> toArray(a: Array<T>): Array<T> {
        val size = this.size
        if (a.size < size) { // Make a new array of a's runtime type, but my contents:
            return queue.copyOf(size) as Array<T>
        }
        queue.copyInto(a as Array<E?>, 0, 0, size)
        if (a.size > size) {
            a[size] = null
        }
        return a
    }

    /**
     * Returns an iterator over the elements in this queue. The iterator
     * does not return the elements in any particular order.
     *
     * @return an iterator over the elements in this queue
     */
    override fun iterator(): MutableIterator<E> {
        return Itr()
    }

    private inner class Itr // prevent access constructor creation
        : MutableIterator<E> {
        /**
         * Index (into queue array) of element to be returned by
         * subsequent call to next.
         */
        private var cursor = 0

        /**
         * Index of element returned by most recent call to next,
         * unless that element came from the forgetMeNot list.
         * Set to -1 if element is deleted by a call to remove.
         */
        private var lastRet = -1

        /**
         * A queue of elements that were moved from the unvisited portion of
         * the heap into the visited portion as a result of "unlucky" element
         * removals during the iteration.  (Unlucky element removals are those
         * that require a siftup instead of a siftdown.)  We must visit all of
         * the elements in this list to complete the iteration.  We do this
         * after we've completed the "normal" iteration.
         *
         * We expect that most iterations, even those involving removals,
         * will not need to store elements in this field.
         */
        private var forgetMeNot: ArrayDeque<E>? = null

        /**
         * Element returned by the most recent call to next iff that
         * element was drawn from the forgetMeNot list.
         */
        private var lastRetElt: E? = null

        /**
         * The modCount value that the iterator believes that the backing
         * Queue should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        private var expectedModCount = modCount

        override fun hasNext(): Boolean {
            return cursor < size || !forgetMeNot.isNullOrEmpty()
        }

        override fun next(): E {
            if (expectedModCount != modCount) {
                throw ConcurrentModificationException()
            }
            if (cursor < size) {
                lastRet = cursor++
                return queue[lastRet] as E
            }
            if (forgetMeNot != null) {
                lastRet = -1
                lastRetElt = forgetMeNot!!.removeFirstOrNull()
                if (lastRetElt != null) {
                    return lastRetElt as E
                }
            }
            throw NoSuchElementException()
        }

        override fun remove() {
            if (expectedModCount != modCount) {
                throw ConcurrentModificationException()
            }
            if (lastRet != -1) {
                val moved = removeAt(lastRet)
                lastRet = -1
                if (moved == null) {
                    cursor--
                } else {
                    if (forgetMeNot == null) {
                        forgetMeNot = ArrayDeque()
                    }
                    forgetMeNot!!.add(moved)
                }
            } else if (lastRetElt != null) {
                removeEq(lastRetElt!!)
                lastRetElt = null
            } else {
                throw IllegalStateException()
            }
            expectedModCount = modCount
        }
    }

    /**
     * Removes all of the elements from this priority queue.
     * The queue will be empty after this call returns.
     */
    override fun clear() {
        modCount++
        val es = queue
        var i = 0
        val n = size
        while (i < n) {
            es[i] = null
            i++
        }
        size = 0
    }

    override fun poll(): E? {
        val es: Array<E?> = queue
        val result: E? = es[0]

        if (result != null) {
            modCount++
            val n: Int = --size
            val x = es[n] as E
            es[n] = null
            if (n > 0) {
                val cmp: Comparator<in E>? = comparator
                if (cmp == null) {
                    siftDownComparable(0, x, es, n)
                } else {
                    siftDownUsingComparator(0, x, es, n, cmp)
                }
            }
        }
        return result
    }

    /**
     * Removes the ith element from queue.
     *
     * Normally this method leaves the elements at up to i-1,
     * inclusive, untouched.  Under these circumstances, it returns
     * null.  Occasionally, in order to maintain the heap invariant,
     * it must swap a later element of the list with one earlier than
     * i.  Under these circumstances, this method returns the element
     * that was previously at the end of the list and is now at some
     * position before i. This fact is used by iterator.remove so as to
     * avoid missing traversing elements.
     */
    fun removeAt(i: Int): E? {
        // assert i >= 0 && i < size;
        val es = queue
        modCount++
        val s = --size
        if (s == i) // removed last element
            es[i] = null
        else {
            val moved = es[s] as E
            es[s] = null
            siftDown(i, moved)
            if (es[i] === moved) {
                siftUp(i, moved)
                if (es[i] !== moved) {
                    return moved
                }
            }
        }
        return null
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * promoting x up the tree until it is greater than or equal to
     * its parent, or is the root.
     *
     * To simplify and speed up coercions and comparisons, the
     * Comparable and Comparator versions are separated into different
     * methods that are otherwise identical. (Similarly for siftDown.)
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private fun siftUp(k: Int, x: E) {
        if (comparator != null) {
            siftUpUsingComparator(k, x, queue, comparator)
        } else {
            siftUpComparable(k, x, queue)
        }
    }

    /**
     * Inserts item x at position k, maintaining heap invariant by
     * demoting x down the tree repeatedly until it is less than or
     * equal to its children or is a leaf.
     *
     * @param k the position to fill
     * @param x the item to insert
     */
    private fun siftDown(k: Int, x: E) {
        if (comparator != null) {
            siftDownUsingComparator(k, x, queue, size, comparator)
        } else {
            siftDownComparable(k, x, queue, size)
        }
    }

    /**
     * Establishes the heap invariant (described above) in the entire tree,
     * assuming nothing about the order of the elements prior to the call.
     * This classic algorithm due to Floyd (1964) is known to be O(size).
     */
    private fun heapify() {
        val es = queue
        val n = size
        var i = (n ushr 1) - 1
        val cmp: Comparator<in E>? = comparator
        if (cmp == null) {
            while (i >= 0) {
                siftDownComparable(i, es[i] as E, es, n)
                i--
            }
        } else {
            while (i >= 0) {
                siftDownUsingComparator(i, es[i] as E, es, n, cmp)
                i--
            }
        }
    }

    /**
     * Returns the comparator used to order the elements in this
     * queue, or `null` if this queue is sorted according to
     * the [natural ordering][Comparable] of its elements.
     *
     * @return the comparator used to order this queue, or
     * `null` if this queue is sorted according to the
     * natural ordering of its elements
     */
    fun comparator(): Comparator<in E>? {
        return comparator
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    /*
    override fun removeIf(filter: Predicate<in E>): Boolean {
        return bulkRemove(filter)
    }
     */

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    override fun removeAll(c: Collection<E>): Boolean {
        return bulkRemove { e: E? -> c.contains(e) }
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    override fun retainAll(c: Collection<E>): Boolean {
        return bulkRemove { e: E? -> !c.contains(e) }
    }

    /** Implementation of bulk remove methods.  */
    private fun bulkRemove(filter: (E) -> Boolean): Boolean {
        val expectedModCount = ++modCount
        val es = queue
        val end = size
        // Optimize for initial run of survivors
        var i = 0
        while (i < end && !filter(es[i] as E)) {
            i++
        }
        if (i >= end) {
            if (modCount != expectedModCount) {
                throw ConcurrentModificationException()
            }
            return false
        }
        // Tolerate predicates that reentrantly access the collection for
        // read (but writers still get CME), so traverse once to find
        // elements to delete, a second pass to physically expunge.
        val beg = i
        val deathRow = nBits(end - beg)
        deathRow[0] = 1L // set bit 0
        i = beg + 1
        while (i < end) {
            if (filter(es[i] as E)) {
                setBit(deathRow, i - beg)
            }
            i++
        }
        if (modCount != expectedModCount) {
            throw ConcurrentModificationException()
        }
        var w = beg
        i = beg
        while (i < end) {
            if (isClear(deathRow, i - beg)) es[w++] = es[i]
            i++
        }
        i = w.also { size = it }
        while (i < end) {
            es[i] = null
            i++
        }
        heapify()
        return true
    }

    /**
     * @throws NullPointerException {@inheritDoc}
     */
    /*
    override fun forEach(action: Consumer<in E>) {
        val expectedModCount = modCount
        val es = queue
        var i = 0
        val n = size
        while (i < n) {
            action.accept(es[i] as E)
            i++
        }
        if (expectedModCount != modCount) {
            throw ConcurrentModificationException()
        }
    }
     */

    companion object {
        private const val DEFAULT_INITIAL_CAPACITY = 11

        /**
         * The maximum length of array to allocate (unless necessary).
         * Some VMs reserve some header words in an array.
         * Attempts to allocate larger arrays may result in
         * `OutOfMemoryError: Requested array size exceeds VM limit`
         */
        const val MAX_ARRAY_LENGTH: Int = Int.MAX_VALUE - 8

        /**
         * Calculates a new array length given an array's current length, a preferred
         * growth value, and a minimum growth value.  If the preferred growth value
         * is less than the minimum growth value, the minimum growth value is used in
         * its place.  If the sum of the current length and the preferred growth
         * value does not exceed [.MAX_ARRAY_LENGTH], that sum is returned.
         * If the sum of the current length and the minimum growth value does not
         * exceed `MAX_ARRAY_LENGTH`, then `MAX_ARRAY_LENGTH` is returned.
         * If the sum does not overflow an int, then `Integer.MAX_VALUE` is
         * returned.  Otherwise, `OutOfMemoryError` is thrown.
         *
         * @param oldLength   current length of the array (must be non negative)
         * @param minGrowth   minimum required growth of the array length (must be
         * positive)
         * @param prefGrowth  preferred growth of the array length (ignored, if less
         * then `minGrowth`)
         * @return the new length of the array
         * @throws OutOfMemoryError if increasing `oldLength` by
         * `minGrowth` overflows.
         */
        fun newLength(oldLength: Int, minGrowth: Int, prefGrowth: Int): Int {
            // assert oldLength >= 0
            // assert minGrowth > 0

            val newLength = (max(minGrowth.toDouble(), prefGrowth.toDouble()) + oldLength).toInt()
            if (newLength - MAX_ARRAY_LENGTH <= 0) {
                return newLength
            }
            return hugeLength(oldLength, minGrowth)
        }

        private fun hugeLength(oldLength: Int, minGrowth: Int): Int {
            val minLength = oldLength + minGrowth
            if (minLength < 0) { // overflow
                throw OutOfMemoryError("Required array length too large")
            }
            if (minLength <= MAX_ARRAY_LENGTH) {
                return MAX_ARRAY_LENGTH
            }
            return Int.MAX_VALUE
        }

        /** Ensures that queue[0] exists, helping peek() and poll().  */
        private fun ensureNonEmpty(es: Array<Any?>): Array<Any?> {
            return if ((es.size > 0)) es else arrayOfNulls(1)
        }

        private fun <T> siftUpComparable(k: Int, x: T, es: Array<T?>) {
            var index = k
            val key = x as Comparable<T>
            while (index > 0) {
                val parentIndex = (index - 1) ushr 1
                val parent = es[parentIndex]
                if (key.compareTo(parent as T) >= 0) {
                    break
                }
                es[index] = parent
                index = parentIndex
            }
            es[index] = key as T
        }

        private fun <T> siftUpUsingComparator(
            k: Int, x: T, es: Array<T?>, cmp: Comparator<in T>
        ) {
            var index = k
            while (index > 0) {
                val parentIndex = (index - 1) ushr 1
                val parent = es[parentIndex]
                if (cmp.compare(x, parent as T) >= 0) {
                    break
                }
                es[index] = parent
                index = parentIndex
            }
            es[index] = x
        }

        private fun <T> siftDownComparable(k: Int, x: T, es: Array<T?>, n: Int) {
            // assert n > 0;
            var index = k
            val key = x as Comparable<T>
            val half = n ushr 1 // loop while a non-leaf
            while (index < half) {
                var childIndex = (index shl 1) + 1 // assume left child is least
                var child = es[childIndex]
                val rightIndex = childIndex + 1
                if (rightIndex < n && (child as Comparable<T>).compareTo(es[rightIndex] as T) > 0) {
                    childIndex = rightIndex
                    child = es[childIndex]
                }
                if (key.compareTo(child as T) <= 0) {
                    break
                }
                es[index] = child
                index = childIndex
            }
            es[index] = key as T
        }

        private fun <T> siftDownUsingComparator(
            k: Int, x: T, es: Array<T?>, n: Int, cmp: Comparator<in T>
        ) {
            // assert n > 0;
            var index = k
            val half = n ushr 1
            while (index < half) {
                var childIndex = (index shl 1) + 1
                var child = es[childIndex]
                val rightIndex = childIndex + 1
                if (rightIndex < n && cmp.compare(child as T, es[rightIndex] as T) > 0) {
                    childIndex = rightIndex
                    child = es[childIndex]
                }
                if (cmp.compare(x, child as T) <= 0) {
                    break
                }
                es[index] = child
                index = childIndex
            }
            es[index] = x
        }

        // A tiny bit set implementation
        private fun nBits(n: Int): LongArray {
            return LongArray(((n - 1) shr 6) + 1)
        }

        private fun setBit(bits: LongArray, i: Int) {
            bits[i shr 6] = bits[i shr 6] or (1L shl i)
        }

        private fun isClear(bits: LongArray, i: Int): Boolean {
            return (bits[i shr 6] and (1L shl i)) == 0L
        }
    }
}
