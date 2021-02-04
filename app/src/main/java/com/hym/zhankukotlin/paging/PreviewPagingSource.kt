package com.hym.zhankukotlin.paging

import androidx.paging.PagingSource
import com.hym.zhankukotlin.network.NetworkService
import com.hym.zhankukotlin.network.PreviewItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

class PreviewPagingSource(private val networkService: NetworkService) :
    PagingSource<String, PreviewItem>() {
    private val singleThreadDispatcher: CoroutineDispatcher = newSingleThreadContext(toString())
    private val previewItemSet: MutableSet<PreviewItem> = LinkedHashSet()

    override suspend fun load(params: LoadParams<String>): LoadResult<String, PreviewItem> {
        try {
            val url = params.key ?: return LoadResult.Error(IllegalArgumentException("Empty url!"))
            val result = networkService.getPreviewResult(url)
            val urlBase = url.substringBeforeLast('!')
            val curPage = result.pagedArr[0]
            val maxPage = result.pagedArr[1]
            val prevKey = null /*if (curPage == 1) null else urlBase + '!' + (curPage - 1)*/
            val nextKey = if (curPage == maxPage) null else urlBase + '!' + (curPage + 1)
            val previewItems = result.previewItems as? MutableList ?: ArrayList(result.previewItems)
            withContext(singleThreadDispatcher) {
                val it = previewItems.iterator()
                while (it.hasNext()) {
                    if (previewItemSet.contains(it.next())) {
                        it.remove()
                    }
                }
                previewItemSet.addAll(previewItems)
            }
            return LoadResult.Page(
                data = previewItems,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    companion object {
        val TAG = PreviewPagingSource::class.java.simpleName
    }
}