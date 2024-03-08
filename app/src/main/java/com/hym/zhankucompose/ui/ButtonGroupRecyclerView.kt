package com.hym.zhankucompose.ui

import android.content.Context
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.ClassLoaderCreator
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.BoolRes
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import androidx.customview.view.AbsSavedState
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import com.hym.zhankucompose.R
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.*

class ButtonGroupRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.buttonGroupRecyclerViewStyle
) : RecyclerView(
    MaterialThemeOverlay.wrap(context, attrs, defStyleAttr, DEF_STYLE_RES),
    attrs,
    defStyleAttr
) {
    /**
     * Interface definition for a callback to be invoked when a [MaterialButton] is checked or
     * unchecked in this group.
     */
    interface OnButtonCheckedListener {
        /**
         * Called when a [MaterialButton] in this group is checked or unchecked.
         *
         * @param group      The group in which the MaterialButton's checked state was changed
         * @param checkedPos The position of the MaterialButton whose check state changed
         * @param isChecked  Whether the MaterialButton is currently checked
         */
        fun onButtonChecked(group: ButtonGroupRecyclerView, checkedPos: Int, isChecked: Boolean)
    }

    private val mCheckedStateTracker = CheckedStateTracker()
    private val mOnButtonCheckedListeners = LinkedHashSet<OnButtonCheckedListener>()
    private var mSkipCheckedStateTracker = false
    private var mSingleSelection = false
    /**
     * Returns whether we prevent all child buttons from being deselected.
     *
     * @attr ref R.styleable#RadioGroupRecyclerView_selectionRequired
     */
    /**
     * Sets whether we prevent all child buttons from being deselected.
     *
     * @attr ref R.styleable#RadioGroupRecyclerView_selectionRequired
     */
    var isSelectionRequired: Boolean
    private var mCheckedItemId = NO_ID
    private val mDefaultCheckedPos: Int
    private val mCheckedItemIdSet: SortedSet<Long> = TreeSet()

    override fun onFinishInflate() {
        super.onFinishInflate()

        // Checks the appropriate button as requested via XML
        if (mCheckedItemId != NO_ID) checkForced(mCheckedItemId)
    }

    /**
     * This override prohibits Views other than [MaterialButton] to be added.
     */
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child !is MaterialButton) {
            Log.e(LOG_TAG, "Child views must be of type MaterialButton.")
            return
        }
        super.addView(child, index, params)
        // Sets sensible default values and an internal checked change listener for this child
        setupButtonChild(child)

        // Reorders children if a checked child was added to this layout
        if (child.isChecked) {
            val childItemId = getChildItemId(child)
            updateCheckedStates(childItemId, true)
            setCheckedItemId(childItemId)
        } else if (isChildChecked(child) || (mCheckedItemIdSet.isEmpty()
                    && mDefaultCheckedPos == getChildLayoutPosition(child))
        ) {
            MaterialButtonHelper.setButtonCheckedWithoutNotifyListeners(child, true)
            val childItemId = getChildItemId(child)
            updateCheckedStates(childItemId, true)
            setCheckedItemId(childItemId)
        }
    }

    override fun onViewRemoved(child: View) {
        super.onViewRemoved(child)
        (child as MaterialButton).removeOnCheckedChangeListener(mCheckedStateTracker)
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = SavedState(super.onSaveInstanceState()!!)
        state.checkedItemId = mCheckedItemId
        state.checkedItemIdList = ArrayList(mCheckedItemIdSet)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }
        super.onRestoreInstanceState(state.superState)
        resetChecked()
        setCheckedItemId(state.checkedItemId, false)
        for (checkedItemId in state.checkedItemIdList) {
            setCheckedItemId(checkedItemId, false)
        }
    }

    private class SavedState : AbsSavedState {
        var checkedItemId: Long = 0
        var checkedItemIdList: List<Long> = emptyList()

        /**
         * Constructor called from [ButtonGroupRecyclerView.onSaveInstanceState]
         */
        constructor(superState: Parcelable) : super(superState)

        /**
         * Constructor called from [.CREATOR]
         */
        private constructor(parcel: Parcel, loader: ClassLoader?) : super(parcel, loader) {
            checkedItemId = parcel.readLong()
            checkedItemIdList = parcel.readArrayList(null) as List<Long>
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            super.writeToParcel(dest, flags)
            dest.writeLong(checkedItemId)
            dest.writeList(checkedItemIdList)
        }

        companion object CREATOR : ClassLoaderCreator<SavedState> {
            override fun createFromParcel(parcel: Parcel, loader: ClassLoader): SavedState {
                return SavedState(parcel, loader)
            }

            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel, null)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun getAccessibilityClassName(): CharSequence {
        return ButtonGroupRecyclerView::class.java.name
    }

    /**
     * Sets the [MaterialButton] whose id is passed in to the checked state. If this
     * RadioGroupView is in [single selection mode][.isSingleSelection], then all
     * other MaterialButtons in this group will be unchecked. Otherwise, other MaterialButtons will
     * retain their checked state.
     *
     * @param itemId View item id of [MaterialButton] to set checked
     * @see .uncheck
     * @see .clearChecked
     * @see .getCheckedButtonItemIds
     * @see .getCheckedButtonItemId
     */
    fun check(itemId: Long) {
        if (itemId == mCheckedItemId) return
        checkForced(itemId)
    }

    /**
     * Sets the [MaterialButton] whose id is passed in to the unchecked state.
     *
     * @param itemId View item id of [MaterialButton] to set unchecked
     * @see .check
     * @see .clearChecked
     * @see .getCheckedButtonItemIds
     * @see .getCheckedButtonItemId
     */
    fun uncheck(itemId: Long) {
        setCheckedStateForView(itemId, false)
        updateCheckedStates(itemId, false)
        mCheckedItemId = NO_ID
        // recordCheckedItemId(mCheckedItemId, true)
        dispatchOnButtonChecked(itemId, false)
    }

    /**
     * Clears the selections. When the selections are cleared, no [MaterialButton] in this
     * group is checked and [.getCheckedButtonItemIds] returns an empty list.
     *
     * @see .check
     * @see .uncheck
     * @see .getCheckedButtonItemIds
     * @see .getCheckedButtonItemId
     */
    fun clearChecked() {
        mSkipCheckedStateTracker = true
        for (i in 0 until childCount) {
            val child = getChildButton(i)
            child.isChecked = false
            dispatchOnButtonChecked(getChildLayoutPosition(child), false)
        }
        mSkipCheckedStateTracker = false
        mCheckedItemIdSet.clear()
        setCheckedItemId(NO_ID)
    }

    /**
     * When in [single selection mode][.isSingleSelection], returns the identifier of the
     * selected button in this group. Upon empty selection, the returned value is [ ][RecyclerView.NO_ID].
     * If not in single selection mode, the return value is [RecyclerView.NO_ID].
     *
     * @return The item id of the selected [MaterialButton] in this group in [ ][.isSingleSelection]. When not in [single][.isSingleSelection], returns [RecyclerView.NO_ID].
     * @attr ref R.styleable#RadioGroupRecyclerView_checkedButton
     * @see .check
     * @see .uncheck
     * @see .clearChecked
     * @see .getCheckedButtonItemIds
     */
    val checkedButtonItemId: Long
        get() = if (mSingleSelection) mCheckedItemId else NO_ID

    /**
     * Returns the identifiers of the selected [MaterialButton]s in this group. Upon empty
     * selection, the returned value is an empty list.
     *
     * @return The item ids of the selected [MaterialButton]s in this group. When in [ ][.isSingleSelection], returns a list with a single item id. When no
     * [MaterialButton]s are selected, returns an empty list.
     * @see .check
     * @see .uncheck
     * @see .clearChecked
     * @see .getCheckedButtonItemId
     */
    val checkedButtonItemIds: MutableList<Long>
        get() = ArrayList(mCheckedItemIdSet)

    /**
     * Add a listener that will be invoked when the check state of a [MaterialButton] in this
     * group changes. See [OnButtonCheckedListener].
     *
     *
     * Components that add a listener should take care to remove it when finished via [ ][.removeOnButtonCheckedListener].
     *
     * @param listener listener to add
     */
    fun addOnButtonCheckedListener(listener: OnButtonCheckedListener) {
        mOnButtonCheckedListeners.add(listener)
    }

    /**
     * Remove a listener that was previously added via [ ][.addOnButtonCheckedListener].
     *
     * @param listener listener to remove
     */
    fun removeOnButtonCheckedListener(listener: OnButtonCheckedListener) {
        mOnButtonCheckedListeners.remove(listener)
    }

    /** Remove all previously added [OnButtonCheckedListener]s.  */
    fun clearOnButtonCheckedListeners() {
        mOnButtonCheckedListeners.clear()
    }

    /**
     * Returns whether this group only allows a single button to be checked.
     *
     * @return whether this group only allows a single button to be checked
     * @attr ref R.styleable#RadioGroupRecyclerView_singleSelection
     */
    /**
     * Sets whether this group only allows a single button to be checked.
     *
     *
     * Calling this method results in all the buttons in this group to become unchecked.
     *
     * @param isSingleSelection whether this group only allows a single button to be checked
     * @attr ref R.styleable#RadioGroupRecyclerView_singleSelection
     */
    var isSingleSelection: Boolean
        get() = mSingleSelection
        set(singleSelection) {
            if (mSingleSelection != singleSelection) {
                mSingleSelection = singleSelection
                clearChecked()
            }
        }

    /**
     * Sets whether this group only allows a single button to be checked.
     *
     *
     * Calling this method results in all the buttons in this group to become unchecked.
     *
     * @param id boolean resource ID of whether this group only allows a single button to be checked
     * @attr ref R.styleable#RadioGroupRecyclerView_singleSelection
     */
    fun setSingleSelection(@BoolRes id: Int) {
        isSingleSelection = resources.getBoolean(id)
    }

    private fun setCheckedStateForView(itemId: Long, checked: Boolean) {
        val holder = findViewHolderForItemId(itemId) ?: return
        val checkedView = holder.itemView
        mSkipCheckedStateTracker = true
        (checkedView as MaterialButton).isChecked = checked
        recordCheckedItemId(itemId, checked)
        mSkipCheckedStateTracker = false
    }

    private fun setCheckedItemId(checkedItemId: Long) {
        setCheckedItemId(checkedItemId, true)
    }

    private fun setCheckedItemId(checkedItemId: Long, dispatch: Boolean) {
        mCheckedItemId = checkedItemId
        recordCheckedItemId(checkedItemId, true)
        if (dispatch) dispatchOnButtonChecked(checkedItemId, true)
    }

    private fun recordCheckedItemId(checkedItemId: Long, checked: Boolean) {
        if (checkedItemId == NO_ID) return
        if (checked) mCheckedItemIdSet.add(checkedItemId)
        else mCheckedItemIdSet.remove(checkedItemId)
    }

    private fun resetChecked() {
        mCheckedItemId = NO_ID
        mCheckedItemIdSet.clear()
    }

    private fun isChildChecked(child: View): Boolean {
        return getChildViewHolder(child)?.let {
            mCheckedItemIdSet.contains(it.itemId)
        } ?: false
    }

    private fun getChildButton(index: Int): MaterialButton {
        return getChildAt(index) as MaterialButton
    }

    private val firstVisibleChildIndex: Int
        get() {
            return (0 until childCount).firstOrNull {
                isChildVisible(it)
            } ?: -1
        }

    private val lastVisibleChildIndex: Int
        get() {
            return (childCount - 1 downTo 0).firstOrNull {
                isChildVisible(it)
            } ?: -1
        }

    private fun isChildVisible(i: Int): Boolean {
        val child = getChildAt(i)
        return child.visibility != GONE
    }

    private val visibleButtonCount: Int
        get() = (0 until childCount).count {
            isChildVisible(it)
        }

    private fun getIndexWithinVisibleButtons(child: View): Int {
        var index = 0
        for (i in 0 until childCount) {
            if (getChildAt(i) === child) return index
            if (isChildVisible(i)) index++
        }
        return -1
    }

    /**
     * When a checked child is added, or a child is clicked, updates checked state and draw order of
     * children to draw all checked children on top of all unchecked children.
     *
     *
     * If `singleSelection` is true, this will unselect any other children as well.
     *
     *
     * If `selectionRequired` is true, and the last child is unchecked it will undo the
     * deselection.
     *
     * @param childItemId    item id of child whose checked state may have changed
     * @param childIsChecked Whether the child is checked
     * @return Whether the checked state for childId has changed.
     */
    private fun updateCheckedStates(childItemId: Long, childIsChecked: Boolean): Boolean {
        val checkedButtonPositions = checkedButtonItemIds
        if (isSelectionRequired && checkedButtonPositions.isEmpty()) {
            // undo deselection
            setCheckedStateForView(childItemId, true)
            mCheckedItemId = childItemId
            recordCheckedItemId(childItemId, true)
            return false
        }

        // un select previous selection
        if (childIsChecked && mSingleSelection) {
            checkedButtonPositions.remove(childItemId)
            for (buttonItemId in checkedButtonPositions) {
                setCheckedStateForView(buttonItemId, false)
                dispatchOnButtonChecked(buttonItemId, false)
            }
        }
        return true
    }

    private fun dispatchOnButtonChecked(buttonItemId: Long, checked: Boolean) {
        findViewHolderForItemId(buttonItemId)?.let {
            dispatchOnButtonChecked(it.layoutPosition, checked)
        }
    }

    private fun dispatchOnButtonChecked(buttonPos: Int, checked: Boolean) {
        for (listener in mOnButtonCheckedListeners) {
            listener.onButtonChecked(this, buttonPos, checked)
        }
    }

    private fun checkForced(checkedItemId: Long) {
        setCheckedStateForView(checkedItemId, true)
        updateCheckedStates(checkedItemId, true)
        setCheckedItemId(checkedItemId)
    }

    /**
     * Sets sensible default values for [MaterialButton] child of this group, set child to
     * `checkable`, and set internal checked change listener for this child.
     *
     * @param buttonChild [MaterialButton] child to set up to be added to this [                    ]
     */
    private fun setupButtonChild(buttonChild: MaterialButton) {
        buttonChild.maxLines = 1
        buttonChild.ellipsize = TextUtils.TruncateAt.END
        buttonChild.isCheckable = true
        buttonChild.addOnCheckedChangeListener(mCheckedStateTracker)

        // Enables surface layer drawing for semi-opaque strokes
        // buttonChild.setShouldDrawSurfaceColorStroke(true)
        MaterialButtonHelper.setShouldDrawSurfaceColorStroke(buttonChild, true)
    }

    private inner class CheckedStateTracker : MaterialButton.OnCheckedChangeListener {
        override fun onCheckedChanged(button: MaterialButton, isChecked: Boolean) {
            // Prevents infinite recursion
            if (mSkipCheckedStateTracker) return
            val childItemId = getChildItemId(button)
            if (mSingleSelection) {
                mCheckedItemId = if (isChecked) childItemId else NO_ID
                if (!isChecked) recordCheckedItemId(childItemId, false)
                recordCheckedItemId(mCheckedItemId, true)
            } else {
                recordCheckedItemId(childItemId, isChecked)
            }
            val buttonCheckedStateChanged = updateCheckedStates(childItemId, isChecked)
            if (buttonCheckedStateChanged) {
                // Dispatch button.isChecked instead of isChecked in case its checked state was
                // updated internally.
                dispatchOnButtonChecked(childItemId, button.isChecked)
            }
            invalidate()
        }
    }

    private val mAdapterDataObserver = AdapterDataObserver()

    private inner class AdapterDataObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            var checkedItemId = NO_ID
            val checkedItemIds: MutableList<Long> = LinkedList()
            val adapter = adapter
            if (adapter != null) {
                val count = adapter.itemCount
                for (pos in 0 until count) {
                    val holder = findViewHolderForAdapterPosition(pos) ?: continue
                    val itemId = holder.itemId
                    if (mCheckedItemId == itemId) {
                        checkedItemId = itemId
                    }
                    if (mCheckedItemIdSet.contains(itemId)) {
                        checkedItemIds.add(itemId)
                    }
                }
                setItemViewCacheSize(count)
            } else {
                setItemViewCacheSize(0)
            }
            resetChecked()
            if (checkedItemId != NO_ID) {
                mCheckedItemId = checkedItemId
            }
            mCheckedItemIdSet.addAll(checkedItemIds)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            onItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            onChanged()
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            onChanged()
        }
    }

    override fun swapAdapter(adapter: Adapter<*>?, removeAndRecycleExistingViews: Boolean) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(mAdapterDataObserver)
        super.swapAdapter(adapter, removeAndRecycleExistingViews)
        if (adapter == null) return
        setItemViewCacheSize(adapter.itemCount)
        adapter.registerAdapterDataObserver(mAdapterDataObserver)
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(mAdapterDataObserver)
        super.setAdapter(adapter)
        if (adapter == null) return
        setItemViewCacheSize(adapter.itemCount)
        adapter.registerAdapterDataObserver(mAdapterDataObserver)
    }

    abstract class ButtonCheckedAdapter<VH : ViewHolder> : Adapter<VH>() {
        private val mButtonListenerMap: MutableMap<VH, MaterialButton.OnCheckedChangeListener> =
            WeakHashMap()

        init {
            setHasStableIds(true)
        }

        abstract override fun getItemId(position: Int): Long

        abstract fun getOnCheckedChangeListener(
            holder: VH,
            position: Int
        ): MaterialButton.OnCheckedChangeListener?

        @CallSuper
        override fun onBindViewHolder(holder: VH, position: Int) {
            val button = holder.itemView as MaterialButton
            val oldListener = mButtonListenerMap.remove(holder)
            if (oldListener != null) {
                button.removeOnCheckedChangeListener(oldListener)
            }
            val newListener = getOnCheckedChangeListener(holder, position)
            if (newListener != null) {
                mButtonListenerMap[holder] = newListener
                button.addOnCheckedChangeListener(newListener)
            }
        }

        @CallSuper
        override fun onViewRecycled(holder: VH) {
            mButtonListenerMap.remove(holder)
            val button = holder.itemView as MaterialButton
            button.clearOnCheckedChangeListeners()
            if (button.isChecked) button.toggle()
        }
    }

    private object MaterialButtonHelper {
        private var setShouldDrawSurfaceColorStroke: Method? = try {
            MaterialButton::class.java.getDeclaredMethod(
                "setShouldDrawSurfaceColorStroke", Boolean::class.javaPrimitiveType
            ).apply {
                isAccessible = true
            }
        } catch (e: ReflectiveOperationException) {
            Log.w(LOG_TAG, "get MaterialButton.setShouldDrawSurfaceColorStroke failed", e)
            null
        }

        private var onCheckedChangeListeners: Field? = try {
            MaterialButton::class.java.getDeclaredField("onCheckedChangeListeners").apply {
                isAccessible = true
            }
        } catch (e: ReflectiveOperationException) {
            Log.w(LOG_TAG, "get MaterialButton.onCheckedChangeListeners failed", e)
            null
        }

        fun setShouldDrawSurfaceColorStroke(
            button: MaterialButton,
            shouldDrawSurfaceColorStroke: Boolean
        ) {
            if (setShouldDrawSurfaceColorStroke === null) {
                Log.w(LOG_TAG, "setShouldDrawSurfaceColorStroke failed: method is null")
                return
            }
            try {
                setShouldDrawSurfaceColorStroke?.invoke(button, shouldDrawSurfaceColorStroke)
            } catch (e: ReflectiveOperationException) {
                Log.w(LOG_TAG, "setShouldDrawSurfaceColorStroke failed", e)
            }
        }

        private fun removeAllOnCheckedChangeListeners(button: MaterialButton): Set<MaterialButton.OnCheckedChangeListener> {
            if (onCheckedChangeListeners === null) {
                Log.w(LOG_TAG, "removeAllOnCheckedChangeListeners failed: field is null")
                return emptySet()
            }
            return try {
                val oriSet =
                    onCheckedChangeListeners?.get(button) as MutableSet<MaterialButton.OnCheckedChangeListener>
                val retSet: MutableSet<MaterialButton.OnCheckedChangeListener> = LinkedHashSet()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    oriSet.removeIf { onCheckedChangeListener ->
                        retSet.add(onCheckedChangeListener) // add to retSet
                        true // remove all
                    }
                } else {
                    retSet.addAll(oriSet)
                    oriSet.clear()
                }
                retSet
            } catch (e: ReflectiveOperationException) {
                Log.w(LOG_TAG, "removeAllOnCheckedChangeListeners failed", e)
                emptySet()
            }
        }

        private fun restoreOnCheckedChangeListeners(
            button: MaterialButton,
            set: Set<MaterialButton.OnCheckedChangeListener>
        ) {
            if (set.isEmpty()) return
            if (onCheckedChangeListeners === null) {
                Log.w(LOG_TAG, "restoreOnCheckedChangeListeners failed: field is null")
                return
            }
            try {
                val obj = onCheckedChangeListeners?.get(button)
                (obj as MutableSet<MaterialButton.OnCheckedChangeListener>).addAll(set)
            } catch (e: ReflectiveOperationException) {
                Log.w(LOG_TAG, "restoreOnCheckedChangeListeners failed", e)
            }
        }

        fun setButtonCheckedWithoutNotifyListeners(button: MaterialButton, checked: Boolean) {
            val set = removeAllOnCheckedChangeListeners(button)
            button.isChecked = checked
            restoreOnCheckedChangeListeners(button, set)
        }
    }

    companion object {
        private const val LOG_TAG = "ButtonGroupRecyclerView"
        private const val DEF_STYLE_RES = R.style.Widget_ButtonGroupRecyclerView
    }

    init {
        // Ensure we are using the correctly themed context rather than the context that was
        // passed in.
        val a = getContext().obtainStyledAttributes(
            attrs, R.styleable.ButtonGroupRecyclerView, defStyleAttr, DEF_STYLE_RES
        )
        isSingleSelection = a.getBoolean(R.styleable.ButtonGroupRecyclerView_singleSelection, false)
        mDefaultCheckedPos =
            a.getInteger(R.styleable.ButtonGroupRecyclerView_checkedButtonPos, NO_POSITION)
        recordCheckedItemId(mCheckedItemId, true)
        isSelectionRequired =
            a.getBoolean(R.styleable.ButtonGroupRecyclerView_selectionRequired, false)
        isChildrenDrawingOrderEnabled = true
        a.recycle()
        ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES)
    }
}