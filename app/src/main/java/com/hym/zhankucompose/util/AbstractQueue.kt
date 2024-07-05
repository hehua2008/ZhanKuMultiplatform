package com.hym.zhankucompose.util

/**
 * @author hehua2008
 * @date 2024/7/5
 */
abstract class AbstractQueue<E>
/**
 * Constructor for use by subclasses.
 */
protected constructor() : AbstractMutableCollection<E>(), Queue<E> {
    /**
     * Inserts the specified element into this queue if it is possible to do so
     * immediately without violating capacity restrictions, returning
     * `true` upon success and throwing an `IllegalStateException`
     * if no space is currently available.
     *
     *
     * This implementation returns `true` if `offer` succeeds,
     * else throws an `IllegalStateException`.
     *
     * @param e the element to add
     * @return `true` (as specified by [Collection.add])
     * @throws IllegalStateException if the element cannot be added at this
     * time due to capacity restrictions
     * @throws ClassCastException if the class of the specified element
     * prevents it from being added to this queue
     * @throws NullPointerException if the specified element is null and
     * this queue does not permit null elements
     * @throws IllegalArgumentException if some property of this element
     * prevents it from being added to this queue
     */
    override fun add(e: E): Boolean {
        if (offer(e)) return true
        else throw IllegalStateException("Queue full")
    }

    /**
     * Retrieves and removes the head of this queue.  This method differs
     * from [poll][.poll] only in that it throws an exception if this
     * queue is empty.
     *
     *
     * This implementation returns the result of `poll`
     * unless the queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    override fun remove(): E {
        val x = poll()
        if (x != null) return x
        else throw NoSuchElementException()
    }

    /**
     * Retrieves, but does not remove, the head of this queue.  This method
     * differs from [peek][.peek] only in that it throws an exception if
     * this queue is empty.
     *
     *
     * This implementation returns the result of `peek`
     * unless the queue is empty.
     *
     * @return the head of this queue
     * @throws NoSuchElementException if this queue is empty
     */
    override fun element(): E {
        val x = peek()
        if (x != null) return x
        else throw NoSuchElementException()
    }

    /**
     * Removes all of the elements from this queue.
     * The queue will be empty after this call returns.
     *
     *
     * This implementation repeatedly invokes [poll][.poll] until it
     * returns `null`.
     */
    override fun clear() {
        while (poll() != null);
    }

    /**
     * Adds all of the elements in the specified collection to this
     * queue.  Attempts to addAll of a queue to itself result in
     * `IllegalArgumentException`. Further, the behavior of
     * this operation is undefined if the specified collection is
     * modified while the operation is in progress.
     *
     *
     * This implementation iterates over the specified collection,
     * and adds each element returned by the iterator to this
     * queue, in turn.  A runtime exception encountered while
     * trying to add an element (including, in particular, a
     * `null` element) may result in only some of the elements
     * having been successfully added when the associated exception is
     * thrown.
     *
     * @param c collection containing elements to be added to this queue
     * @return `true` if this queue changed as a result of the call
     * @throws ClassCastException if the class of an element of the specified
     * collection prevents it from being added to this queue
     * @throws NullPointerException if the specified collection contains a
     * null element and this queue does not permit null elements,
     * or if the specified collection is null
     * @throws IllegalArgumentException if some property of an element of the
     * specified collection prevents it from being added to this
     * queue, or if the specified collection is this queue
     * @throws IllegalStateException if not all the elements can be added at
     * this time due to insertion restrictions
     * @see .add
     */
    override fun addAll(c: Collection<E>): Boolean {
        require(c !== this)
        var modified = false
        for (e in c) if (add(e)) modified = true
        return modified
    }
}
