package com.hym.logcollector.util

import android.os.Parcel
import android.os.Parcelable

/**
 * @author hehua2008
 * @date 2021/8/31
 */
object ParcelExt {
    fun <T : Parcelable?> Parcel.writeParcelableListExt(list: List<T>?, flags: Int) {
        if (list == null) {
            writeInt(-1)
            return
        }
        val size = list.size
        var i = 0
        writeInt(size)
        while (i < size) {
            writeParcelable(list[i], flags)
            i++
        }
    }

    @JvmOverloads
    fun <T : Parcelable?> Parcel.readParcelableListExt(
        list: MutableList<T> = mutableListOf(),
        cl: ClassLoader?
    ): List<T> {
        val size: Int = readInt()
        if (size == -1) {
            list.clear()
            return list
        }
        val listSize = list.size
        var i = 0
        while (i < listSize && i < size) {
            list[i] = readParcelable<Parcelable>(cl) as T
            i++
        }
        while (i < size) {
            list.add(readParcelable<Parcelable>(cl) as T)
            i++
        }
        while (i < listSize) {
            list.removeAt(size)
            i++
        }
        return list
    }
}