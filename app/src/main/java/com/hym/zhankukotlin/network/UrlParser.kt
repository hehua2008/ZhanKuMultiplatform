package com.hym.zhankukotlin.network

class UrlParser private constructor(
    val host: String,
    relPath: String,
    queryMap: Map<String, String>
) {
    val absPath: String
    val relPath: String
    val queryMap: Map<String, String>

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(absPath)
        if (queryMap.isNotEmpty()) {
            sb.append('?')
            for ((key, value) in queryMap) {
                sb.append(key).append('=').append(value).append('&')
            }
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    companion object {
        @JvmStatic
        fun parse(url: String): UrlParser {
            val len = url.length
            var qidx = url.indexOf('?')
            if (qidx == -1) {
                qidx = url.length
            }
            val doubleSlashIdx = url.indexOf("//")
            var pathStartIdx = url.indexOf('/', doubleSlashIdx + 2)
            if (pathStartIdx == -1) {
                pathStartIdx = url.length
            }
            val host = url.substring(0, pathStartIdx)
            val relPath = url.substring(pathStartIdx, qidx)
            val queryMap: MutableMap<String, String> = LinkedHashMap()
            if (qidx < len - 1) {
                val querys = url.substring(qidx + 1).split("&").toTypedArray()
                for (query in querys) {
                    val eidx = query.indexOf('=')
                    if (eidx > 0 && eidx < query.length - 1) {
                        queryMap[query.substring(0, eidx)] = query.substring(eidx + 1)
                    }
                }
            }
            return UrlParser(host, relPath, queryMap)
        }
    }

    init {
        absPath = host + relPath
        this.relPath = relPath
        this.queryMap = queryMap
    }
}