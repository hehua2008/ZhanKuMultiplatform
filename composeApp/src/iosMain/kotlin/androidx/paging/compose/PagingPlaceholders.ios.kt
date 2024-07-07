package androidx.paging.compose

internal actual fun getPagingPlaceholderKey(index: Int): Any {
    return PagingPlaceholderKey(index)
}

private data class PagingPlaceholderKey(private val index: Int)
