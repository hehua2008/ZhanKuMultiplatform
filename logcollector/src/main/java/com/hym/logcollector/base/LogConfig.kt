package com.hym.logcollector.base

import android.os.Parcel
import android.os.Parcelable
import com.hym.logcollector.util.ParcelExt.readParcelableListExt
import com.hym.logcollector.util.ParcelExt.writeParcelableListExt

/**
 * @author hehua2008
 * @date 2021/8/21
 */
data class LogConfig @JvmOverloads constructor(
    val logcatTypes: List<LogcatType> = listOf(LogcatType.ALL),
    val logcatParser: LogParser<*> = LogParser.DEFAULT.THREAD_TIME,
    val logFileParsers: List<LogParser<*>> = LogParser.DEFAULT.values().toList(),
    val logLevelColorMapper: LogLevelColorMapper = LogLevelColorMapper.DEFAULT,
    val logFilePageSize: Int = 100
) : Parcelable {
    companion object CREATOR : Parcelable.Creator<LogConfig> {
        override fun createFromParcel(parcel: Parcel): LogConfig {
            return LogConfig(parcel)
        }

        override fun newArray(size: Int): Array<LogConfig?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(
        parcel.readParcelableListExt(cl = LogcatType::class.java.classLoader),
        parcel.readParcelable<LogParser<*>>(LogParser::class.java.classLoader)!!,
        parcel.readParcelableListExt(cl = LogParser::class.java.classLoader),
        parcel.readParcelable(LogLevelColorMapper::class.java.classLoader)!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelableListExt(logcatTypes, flags)
        parcel.writeParcelable(logcatParser, flags)
        parcel.writeParcelableListExt(logFileParsers, flags)
        parcel.writeParcelable(logLevelColorMapper, flags)
        parcel.writeInt(logFilePageSize)
    }

    override fun describeContents(): Int {
        return 0
    }
}