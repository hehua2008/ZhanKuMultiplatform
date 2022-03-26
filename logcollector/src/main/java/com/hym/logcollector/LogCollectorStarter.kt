package com.hym.logcollector

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.hym.logcollector.base.LogConfig
import com.hym.logcollector.base.LogLevelColorMapper
import com.hym.logcollector.base.LogParser
import com.hym.logcollector.base.LogcatType

/**
 * @author hehua2008
 * @date 2022/3/26
 *
 * Content providers are loaded before the application class is created.
 * [LogCollectorStarter] is used to start [LogcatService] on application start.
 */
class LogCollectorStarter : ContentProvider() {
    override fun onCreate(): Boolean {
        val logConfig = LogConfig(
            logcatTypes = listOf(
                LogcatType.ALL,
                LogcatType("Glide", "(?-i)Glide:"),
                LogcatType("Runtime", "AndroidRuntime")
            ),
            logcatParser = LogParser.DEFAULT.THREAD_TIME,
            logFileParsers = listOf(
                LogParser.DEFAULT.THREAD_TIME,
                LogParser.DEFAULT.ORIGINAL,
                LogParser.DEFAULT.LOGAN_INFO
            ),
            logLevelColorMapper = LogLevelColorMapper.DEFAULT
        )
        LogcatService.start(context!!, logConfig)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}