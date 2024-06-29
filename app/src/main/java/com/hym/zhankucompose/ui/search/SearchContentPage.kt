package com.hym.zhankucompose.ui.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.hym.zhankucompose.MyAppViewModel
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.LabelFlowLayout
import com.hym.zhankucompose.compose.SMALL_PADDING_VALUES
import com.hym.zhankucompose.compose.SimpleRadioGroup
import com.hym.zhankucompose.getAppViewModel
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.PagedLayout
import com.hym.zhankucompose.ui.main.MainViewModel
import com.hym.zhankucompose.ui.main.PreviewLayout
import kotlinx.collections.immutable.toImmutableList

/**
 * @author hehua2008
 * @date 2024/4/23
 */
@Composable
fun SearchContentPage(
    contentType: ContentType,
    onNavigateToDetails: (contentType: ContentType, contentId: String) -> Unit,
    onNavigateToTagList: (author: CreatorObj?, topCate: TopCate?, subCate: SubCate?) -> Unit,
    modifier: Modifier = Modifier,
    pageViewModel: SearchContentPageViewModel = viewModel(key = contentType.text),
    mainViewModel: MainViewModel = viewModel()
) {
    LaunchedEffect(pageViewModel, contentType) {
        pageViewModel.contentType = contentType
    }

    LaunchedEffect(pageViewModel, mainViewModel) {
        mainViewModel.word.collect {
            pageViewModel.setWord(it)
        }
    }

    val lazyPagingItems = pageViewModel.pagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(pageViewModel, lazyPagingItems) {
        pageViewModel.page.collect {
            lazyPagingItems.refresh()
        }
    }

    val topCates = getAppViewModel<MyAppViewModel>().categoryItems
    val topCateList = remember(topCates) {
        mutableListOf(TopCate.All).apply {
            addAll(topCates)
        }.toImmutableList()
    }
    val topCateListNameList = remember(topCateList) {
        topCateList.map { it.name }.toImmutableList()
    }
    val topCate by pageViewModel.topCate.collectAsState(TopCate.All)
    val defaultCategoryIndex = remember(topCateList, topCate) {
        topCateList.indexOf(topCate).let { if (it < 0) 0 else it }
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val labelTextStyle = MaterialTheme.typography.labelLarge.let {
        remember(it, onSurfaceColor) { it.copy(color = onSurfaceColor) }
    }

    val recommendLevels = remember {
        listOf(
            RecommendLevel.ALL_LEVEL,
            RecommendLevel.EDITOR_CHOICE,
            RecommendLevel.ALL_RECOMMEND,
            RecommendLevel.HOME_RECOMMEND
        ).toImmutableList()
    }

    val sortOrders = remember {
        listOf(
            SortOrder.BEST_MATCH,
            SortOrder.LATEST_PUBLISH,
            SortOrder.MOST_RECOMMEND,
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
            LabelFlowLayout(
                labels = topCateListNameList,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = COMMON_PADDING),
                defaultLabelIndex = defaultCategoryIndex,
                itemPadding = SMALL_PADDING_VALUES
            ) {
                pageViewModel.setTopCate(topCateList[it])
            }

            SimpleRadioGroup(
                items = recommendLevels,
                modifier = Modifier.padding(top = COMMON_PADDING),
                onItemSelected = { level ->
                    pageViewModel.setRecommendLevel(level)
                }
            ) { level ->
                Text(
                    text = level.text,
                    maxLines = 1,
                    style = labelTextStyle
                )
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
                pageSizeList = SearchContentPageViewModel.PageSizeList,
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
