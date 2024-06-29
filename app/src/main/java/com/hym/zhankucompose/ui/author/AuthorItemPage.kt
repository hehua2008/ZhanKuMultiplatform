package com.hym.zhankucompose.ui.author

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.SimpleLinkText
import com.hym.zhankucompose.compose.SimpleRadioGroup
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.PagedLayout
import com.hym.zhankucompose.ui.main.PreviewLayout
import com.hym.zhankucompose.ui.webview.WebViewActivity
import kotlinx.collections.immutable.toImmutableList

/**
 * @author hehua2008
 * @date 2024/4/9
 */
@Composable
fun AuthorItemPage(
    author: CreatorObj,
    onNavigateToDetails: (contentType: ContentType, contentId: String) -> Unit,
    onNavigateToTagList: (author: CreatorObj?, topCate: TopCate?, subCate: SubCate?) -> Unit,
    modifier: Modifier = Modifier,
    pageViewModel: AuthorPageViewModel = viewModel(key = author.username)
) {
    LaunchedEffect(pageViewModel, author) {
        pageViewModel.authorUid = author.id
    }

    val context = LocalContext.current
    val lazyPagingItems = pageViewModel.pagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(pageViewModel, lazyPagingItems) {
        pageViewModel.page.collect {
            lazyPagingItems.refresh()
        }
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val labelTextStyle = MaterialTheme.typography.labelLarge.let {
        remember(it, onSurfaceColor) { it.copy(color = onSurfaceColor) }
    }

    val sortOrders = remember {
        listOf(
            SortOrder.LATEST_PUBLISH,
            SortOrder.MOST_RECOMMEND,
            SortOrder.MOST_FAVORITE,
            SortOrder.MOST_COMMENT
        ).toImmutableList()
    }

    val activePage by pageViewModel.page.collectAsState(1)
    val lastPage by pageViewModel.totalPages.collectAsState(1)
    val pageSizeIndex by pageViewModel.pageSizeIndex.collectAsState(0)

    PreviewLayout(
        lazyPagingItems = lazyPagingItems,
        onNavigateToDetails = onNavigateToDetails,
        onNavigateToTagList = onNavigateToTagList,
        modifier = modifier
    ) { headerModifier ->
        Column(
            modifier = headerModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleLinkText(
                link = "https://www.zcool.com.cn/u/${author.id}",
                modifier = Modifier.padding(top = COMMON_PADDING)
            ) {
                val intent = Intent(context, WebViewActivity::class.java)
                    .putExtra(WebViewActivity.WEB_URL, it)
                    .putExtra(WebViewActivity.WEB_TITLE, author.username)
                context.startActivity(intent)
            }

            SimpleRadioGroup(
                items = sortOrders,
                modifier = Modifier.padding(top = COMMON_PADDING),
                onItemSelected = { order ->
                    pageViewModel.setSortOrder(order)
                }
            ) { order ->
                Text(
                    text = order.text,
                    maxLines = 1,
                    style = labelTextStyle
                )
            }

            PagedLayout(
                modifier = Modifier.padding(top = COMMON_PADDING),
                activePage = activePage,
                lastPage = lastPage,
                pageSizeList = AuthorPageViewModel.PageSizeList,
                pageSizeIndex = pageSizeIndex,
                onPreClick = { pageViewModel.setPage(activePage - 1) },
                onNextClick = { pageViewModel.setPage(activePage + 1) },
                onJumpAction = { pageViewModel.setPage(it) }
            ) { index, item ->
                pageViewModel.setPageSizeIndex(index)
            }
        }
    }
}
