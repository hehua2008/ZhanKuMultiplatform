package com.hym.logcollector.util

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * @author hehua2008
 * @date 2021/8/26
 */
object CharsetList : List<Charset> by listOf(
    StandardCharsets.UTF_8,
    StandardCharsets.US_ASCII,
    StandardCharsets.ISO_8859_1,
    StandardCharsets.UTF_16BE,
    StandardCharsets.UTF_16LE,
    StandardCharsets.UTF_16,
    Charset.forName("EUC-JP"),
    Charset.forName("EUC-KR"),
    Charset.forName("GB18030"),
    Charset.forName("GBK")
)