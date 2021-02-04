package com.hym.zhankukotlin.ui

abstract class NameValueAdapter<K, V> : ButtonItemAdapter() {
    protected var mNameValueMap: Map<K, V> = emptyMap()
    protected var mNameValues: Array<Map.Entry<K, V>> = arrayOf()
    protected var mItemIds = LongArray(0)

    override fun getItemCount(): Int {
        return mNameValues.size
    }

    override fun getItemId(position: Int): Long {
        return mItemIds[position]
    }

    fun setNameValueMap(nameValueMap: Map<K, V>) {
        mNameValueMap = nameValueMap
        val size = mNameValueMap.size
        mNameValues = mNameValueMap.entries.toTypedArray()
        mItemIds = LongArray(size)

        for (i in 0 until size) {
            val keyHash = mNameValues[i].key.hashCode().toLong()
            val valueHash = mNameValues[i].value.hashCode().toLong()
            mItemIds[i] = keyHash shl 32 or valueHash
        }

        notifyDataSetChanged()
    }
}