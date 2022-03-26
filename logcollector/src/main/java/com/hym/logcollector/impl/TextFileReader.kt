package com.hym.logcollector.impl

import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.channels.FileChannel
import java.nio.charset.Charset
import java.nio.charset.CoderResult
import java.nio.charset.CodingErrorAction
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author hehua2008
 * @date 2021/8/25
 */
class TextFileReader @JvmOverloads constructor(
    val file: File,
    val charset: Charset = Charsets.UTF_8
) : Closeable {
    companion object {
        private const val MAX_MAP_SIZE = 10 * 1024 * 1024

        private const val STATUS_READY = 0
        private const val STATUS_INITIALIZING = 1
        private const val STATUS_INITIALIZED = 2
        private const val STATUS_CANCELLED = 3

        private fun Charset.decode(input: ByteBuffer, output: CharBuffer): CoderResult {
            return newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .decode(input, output, true)
        }
    }

    private val mFileLength = file.length().toInt()
    private val mRandomAccessFile = RandomAccessFile(file, "r")
    private val mChannel = mRandomAccessFile.channel
    private val mCharBuffer = CharBuffer.allocate(mFileLength.coerceAtMost(MAX_MAP_SIZE))

    //private val mCharOffsetToFileOffset = SparseIntArray()
    private val mCharOffsetToFileOffset: SortedMap<Int, Int> = TreeMap()
    private var mCharOffset = 0

    @Volatile
    private var mLinePositions: List<Int>? = null

    private val mInitStatus = AtomicInteger(STATUS_READY)

    val linePositions: List<Int>
        get() = checkNotNull(mLinePositions) { "initLinePositions() has not been called !" }

    val lineCount: Int
        get() = linePositions.size - 1

    fun initLinePositions(progressCallback: ((progress: Float, cancelled: Boolean) -> Unit)?) {
        check(mInitStatus.compareAndSet(STATUS_READY, STATUS_INITIALIZING)) {
            "mInitStatus.compareAndSet(STATUS_READY, STATUS_INITIALIZING) failed !"
        }
        progressCallback?.invoke(0f, false)
        val tmpLinePositions = mutableListOf<Int>().apply {
            add(0, 0)
            add(1, 0)
        }
        mCharOffsetToFileOffset.apply {
            clear()
            put(0, 0)
        }
        decode()
        val curPos = position()
        seek(0)
        while (true) {
            if (mInitStatus.get() == STATUS_CANCELLED) {
                seek(curPos)
                progressCallback?.invoke(1f, true)
                return
            }
            if (!skipLine()) break
            val pos = position()
            tmpLinePositions.add(pos)
            (pos.toFloat() / mFileLength).let { p ->
                if (p > 0f && p < 1f) progressCallback?.invoke(p, false)
            }
        }
        seek(curPos)
        mLinePositions = Collections.unmodifiableList(tmpLinePositions)
        progressCallback?.invoke(1f, false)
        mInitStatus.set(STATUS_INITIALIZED)
    }

    fun cancelInit(): Boolean {
        return mInitStatus.compareAndSet(STATUS_INITIALIZING, STATUS_CANCELLED)
    }

    fun skipLine(): Boolean {
        var charCount = 0
        var char: Char? = null
        var eol = false
        while (!eol) {
            when (read().also { char = it }) {
                null, '\n' -> eol = true
                '\r' -> {
                    eol = true
                    val cur = position()
                    if (read() != '\n') {
                        seek(cur)
                    }
                }
                else -> char?.let { charCount++ }
            }
        }
        return char != null || charCount != 0
    }

    fun readLine(): String? {
        val charList = mutableListOf<Char>()
        var char: Char? = null
        var eol = false
        while (!eol) {
            when (read().also { char = it }) {
                null, '\n' -> eol = true
                '\r' -> {
                    eol = true
                    val cur = position()
                    if (read() != '\n') {
                        seek(cur)
                    }
                }
                else -> char?.let { charList.add(it) }
            }
        }
        return if (char == null && charList.isEmpty()) {
            null
        } else String(charList.toCharArray())
    }

    private fun position(): Int {
        return mCharOffset + mCharBuffer.position()
    }

    private fun seek(position: Int) {
        var charOffset: Int = mCharOffset
        /*
        for (index in 0 until mCharOffsetToFileOffset.size()) {
            val curCharOffset = mCharOffsetToFileOffset.keyAt(index)
            if (position < curCharOffset) break
            charOffset = curCharOffset
        }
        */
        for (curCharOffset in mCharOffsetToFileOffset.keys) {
            if (position < curCharOffset) break
            charOffset = curCharOffset
        }
        if (mCharOffset != charOffset) {
            mCharOffset = charOffset
            decode()
        }
        mCharBuffer.position(position - mCharOffset)
    }

    fun seekToLine(lineNumber: Int) {
        checkLineNumber(lineNumber)
        seek(linePositions[lineNumber])
    }

    private fun read(): Char? {
        if (mCharBuffer.hasRemaining()) return mCharBuffer.get()
        /*
        val index = mCharOffsetToFileOffset.indexOfKey(mCharOffset)
        val fileOffset = mCharOffsetToFileOffset.valueAt(index)
        if (fileOffset == mFileLength) return null
        mCharOffset = mCharOffsetToFileOffset.keyAt(index + 1)
        */
        val fileOffset = mCharOffsetToFileOffset[mCharOffset]!!
        if (fileOffset == mFileLength) return null
        mCharOffset = mCharOffsetToFileOffset.keys.iterator().let {
            while (mCharOffset != it.next()) continue
            it.next()
        }
        decode()
        return read()
    }

    private fun checkLineNumber(lineNumber: Int) {
        if (lineNumber < 0 || lineNumber > lineCount) {
            throw IndexOutOfBoundsException("LineNumber: $lineNumber, LineCount: $lineCount")
        }
    }

    @Throws(IOException::class)
    override fun close() {
        val e1 = mChannel.runCatching { close() }.exceptionOrNull()
        val e2 = mRandomAccessFile.runCatching { close() }.exceptionOrNull()
        if (e2 != null) throw e2.apply { if (e1 != null) addSuppressed(e1) }
        if (e1 != null) throw e1
        mCharBuffer.clear()
    }

    private fun decode(): CoderResult {
        mCharBuffer.clear()
        val fileOffset = mCharOffsetToFileOffset[mCharOffset]!!
        val nextFileOffset = mFileLength.coerceAtMost(fileOffset + MAX_MAP_SIZE)
        val byteBuffer = mChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileOffset.toLong(), (nextFileOffset - fileOffset).toLong()
        )
        return charset.decode(byteBuffer, mCharBuffer).also {
            if (mInitStatus.get() == STATUS_INITIALIZING) {
                val nextCharOffset = mCharOffset + mCharBuffer.position()
                mCharOffsetToFileOffset[nextCharOffset] = nextFileOffset
            }
            mCharBuffer.flip()
        }
    }
}