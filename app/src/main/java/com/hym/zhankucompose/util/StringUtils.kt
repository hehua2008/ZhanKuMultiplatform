package com.hym.zhankucompose.util

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object StringUtils {
    private const val REPLACEMENT_CHAR = 0xfffd.toChar()
    private val TABLE_UTF8_NEEDED = intArrayOf(
        //0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f
        0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  // 0xc0 - 0xcf
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,  // 0xd0 - 0xdf
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,  // 0xe0 - 0xef
        3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    )
    private val TMP_STRING_BUILDER = object : ThreadLocal<StringBuilder>() {
        override fun initialValue(): StringBuilder {
            return StringBuilder()
        }
    }

    fun newStringFromBytes(
        data: ByteArray, offset: Int = 0, byteCount: Int = data.size,
        charset: Charset = StandardCharsets.UTF_8
    ): String {
        if (offset or byteCount < 0 || byteCount > data.size - offset) {
            throw IndexOutOfBoundsException(
                "length=" + data.size + "; regionStart=" + offset + "; regionLength=" + byteCount
            )
        }
        val value: CharArray
        val length: Int

        // We inline UTF-8, ISO-8859-1, and US-ASCII decoders for speed.
        val canonicalCharsetName = charset.name()
        if (canonicalCharsetName == "UTF-8") {
            /*
            This code converts a UTF-8 byte sequence to a Java String (UTF-16).
            It implements the W3C recommended UTF-8 decoder.
            https://www.w3.org/TR/encoding/#utf-8-decoder

            Unicode 3.2 Well-Formed UTF-8 Byte Sequences
            Code Points        First  Second Third Fourth
            U+0000..U+007F     00..7F
            U+0080..U+07FF     C2..DF 80..BF
            U+0800..U+0FFF     E0     A0..BF 80..BF
            U+1000..U+CFFF     E1..EC 80..BF 80..BF
            U+D000..U+D7FF     ED     80..9F 80..BF
            U+E000..U+FFFF     EE..EF 80..BF 80..BF
            U+10000..U+3FFFF   F0     90..BF 80..BF 80..BF
            U+40000..U+FFFFF   F1..F3 80..BF 80..BF 80..BF
            U+100000..U+10FFFF F4     80..8F 80..BF 80..BF

            Please refer to Unicode as the authority.
            p.126 Table 3-7 in http://www.unicode.org/versions/Unicode10.0.0/ch03.pdf

            Handling Malformed Input
            The maximal subpart should be replaced by a single U+FFFD. Maximal subpart is
            the longest code unit subsequence starting at an unconvertible offset that is either
            1) the initial subsequence of a well-formed code unit sequence, or
            2) a subsequence of length one:
            One U+FFFD should be emitted for every sequence of bytes that is an incomplete prefix
            of a valid sequence, and with the conversion to restart after the incomplete sequence.

            For example, in byte sequence "41 C0 AF 41 F4 80 80 41", the maximal subparts are
            "C0", "AF", and "F4 80 80". "F4 80 80" can be the initial subsequence of "F4 80 80 80",
            but "C0" can't be the initial subsequence of any well-formed code unit sequence.
            Thus, the output should be "A\ufffd\ufffdA\ufffdA".

            Please refer to section "Best Practices for Using U+FFFD." in
            http://www.unicode.org/versions/Unicode10.0.0/ch03.pdf
            */
            val v = CharArray(byteCount)
            var idx = offset
            val last = offset + byteCount
            var s = 0
            var codePoint = 0
            var utf8BytesSeen = 0
            var utf8BytesNeeded = 0
            var lowerBound = 0x80
            var upperBound = 0xbf
            while (idx < last) {
                val b: Int = data[idx++].toInt() and 0xff
                if (utf8BytesNeeded == 0) {
                    if (b and 0x80 == 0) { // ASCII char. 0xxxxxxx
                        v[s++] = b.toChar()
                        continue
                    }
                    if (b and 0x40 == 0) { // 10xxxxxx is illegal as first byte
                        v[s++] = REPLACEMENT_CHAR
                        continue
                    }

                    // 11xxxxxx
                    val tableLookupIndex = b and 0x3f
                    utf8BytesNeeded = TABLE_UTF8_NEEDED[tableLookupIndex]
                    if (utf8BytesNeeded == 0) {
                        v[s++] = REPLACEMENT_CHAR
                        continue
                    }

                    // utf8BytesNeeded
                    // 1: b & 0x1f
                    // 2: b & 0x0f
                    // 3: b & 0x07
                    codePoint = b and (0x3f shr utf8BytesNeeded)
                    when (b) {
                        0xe0 -> {
                            lowerBound = 0xa0
                        }

                        0xed -> {
                            upperBound = 0x9f
                        }

                        0xf0 -> {
                            lowerBound = 0x90
                        }

                        0xf4 -> {
                            upperBound = 0x8f
                        }
                    }
                } else {
                    if (b < lowerBound || b > upperBound) {
                        // The bytes seen are ill-formed. Substitute them with U+FFFD
                        v[s++] = REPLACEMENT_CHAR
                        codePoint = 0
                        utf8BytesNeeded = 0
                        utf8BytesSeen = 0
                        lowerBound = 0x80
                        upperBound = 0xbf
                        /*
                         * According to the Unicode Standard,
                         * "a UTF-8 conversion process is required to never consume well-formed
                         * subsequences as part of its error handling for ill-formed subsequences"
                         * The current byte could be part of well-formed subsequences. Reduce the
                         * index by 1 to parse it in next loop.
                         */idx--
                        continue
                    }
                    lowerBound = 0x80
                    upperBound = 0xbf
                    codePoint = codePoint shl 6 or (b and 0x3f)
                    utf8BytesSeen++
                    if (utf8BytesNeeded != utf8BytesSeen) {
                        continue
                    }

                    // Encode chars from U+10000 up as surrogate pairs
                    if (codePoint < 0x10000) {
                        v[s++] = codePoint.toChar()
                    } else {
                        v[s++] = ((codePoint shr 10) + 0xd7c0).toChar()
                        v[s++] = ((codePoint and 0x3ff) + 0xdc00).toChar()
                    }
                    utf8BytesSeen = 0
                    utf8BytesNeeded = 0
                    codePoint = 0
                }
            }

            // The bytes seen are ill-formed. Substitute them by U+FFFD
            if (utf8BytesNeeded != 0) {
                v[s++] = REPLACEMENT_CHAR
            }
            if (s == byteCount) {
                // We guessed right, so we can use our temporary array as-is.
                value = v
                length = s
            } else {
                // Our temporary array was too big, so reallocate and copy.
                value = CharArray(s)
                length = s
                System.arraycopy(v, 0, value, 0, s)
            }
        } else {
            val cb = charset.decode(ByteBuffer.wrap(data, offset, byteCount))
            length = cb.length
            // The call to newStringFromChars below will copy length bytes out of value, so it does
            // not matter that cb.array().length may be > cb.length() or that a Charset could keep a
            // reference to the CharBuffer it returns and later mutate it.
            value = cb.array()
        }
        val tmpSb = TMP_STRING_BUILDER.get()!!
        tmpSb.setLength(0)
        tmpSb.append(value, 0, length)
        return tmpSb.toString()
    }

    fun generateMD5(input: String): String {
        val md5Digest: MessageDigest = MessageDigest.getInstance("MD5")
        val hash: ByteArray = md5Digest.digest(input.toByteArray())
        val numValue = BigInteger(1, hash)
        return numValue.toString(16)
    }
}