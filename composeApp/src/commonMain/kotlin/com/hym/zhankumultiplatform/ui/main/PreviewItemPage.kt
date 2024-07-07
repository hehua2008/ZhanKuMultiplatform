package com.hym.zhankumultiplatform.ui.main

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
import com.hym.zhankumultiplatform.compose.COMMON_PADDING
import com.hym.zhankumultiplatform.compose.LabelFlowLayout
import com.hym.zhankumultiplatform.compose.SMALL_PADDING_VALUES
import com.hym.zhankumultiplatform.compose.SimpleLinkText
import com.hym.zhankumultiplatform.compose.SimpleRadioGroup
import com.hym.zhankumultiplatform.model.Cate
import com.hym.zhankumultiplatform.model.RecommendLevel
import com.hym.zhankumultiplatform.model.SubCate
import com.hym.zhankumultiplatform.model.TopCate
import com.hym.zhankumultiplatform.navigation.LocalNavListener
import com.hym.zhankumultiplatform.navigation.WebViewArgs
import com.hym.zhankumultiplatform.ui.PagedLayout
import kotlinx.collections.immutable.toImmutableList

/**
 * @author hehua2008
 * @date 2024/4/9
 */
@Composable
fun PreviewItemPage(
    topCate: TopCate,
    modifier: Modifier = Modifier,
    initialSubCate: SubCate? = null,
    pageViewModel: PreviewPageViewModel = viewModel(key = topCate.name) {
        PreviewPageViewModel(topCate = topCate, initialSubCate = initialSubCate)
    }
) {
    val navListener = LocalNavListener.current

    val lazyPagingItems = pageViewModel.pagingFlow.collectAsLazyPagingItems()

    LaunchedEffect(pageViewModel, lazyPagingItems) {
        pageViewModel.page.collect {
            lazyPagingItems.refresh()
        }
    }

    val subCate by pageViewModel.subCate.collectAsState(null)
    val categoryLink = remember(topCate, subCate) {
        updateCategoryLink(topCate, subCate)
    }
    val categories = remember(topCate) {
        mutableListOf<Cate>(topCate).apply {
            addAll(topCate.subCateList)
        }.toImmutableList()
    }
    val categoriesNames = remember(categories) {
        categories.map { it.name }.toImmutableList()
    }
    val defaultCategoryIndex = remember(categories, subCate) {
        categories.indexOf(subCate as Cate?).let { if (it < 0) 0 else it }
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val labelTextStyle = MaterialTheme.typography.labelLarge.let {
        remember(it, onSurfaceColor) { it.copy(color = onSurfaceColor) }
    }

    val recommendLevels = remember {
        listOf(
            RecommendLevel.EDITOR_CHOICE,
            RecommendLevel.ALL_RECOMMEND,
            RecommendLevel.HOME_RECOMMEND,
            RecommendLevel.LATEST_PUBLISH
        ).toImmutableList()
    }

    val activePage by pageViewModel.page.collectAsState(1)
    val lastPage by pageViewModel.totalPages.collectAsState(1)
    val pageSizeIndex by pageViewModel.pageSizeIndex.collectAsState(0)

    PreviewLayout(
        lazyPagingItems = lazyPagingItems,
        modifier = modifier
    ) { headerModifier ->
        Column(
            modifier = headerModifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleLinkText(
                link = categoryLink,
                modifier = Modifier.padding(top = COMMON_PADDING)
            ) {
                navListener.onNavigateToWebView(WebViewArgs(it, (subCate ?: topCate).name))
            }

            LabelFlowLayout(
                labels = categoriesNames,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = COMMON_PADDING),
                defaultLabelIndex = defaultCategoryIndex,
                itemPadding = SMALL_PADDING_VALUES
            ) {
                pageViewModel.setSubCate(if (it == 0) null else topCate.subCateList[it - 1])
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

            PagedLayout(
                modifier = Modifier.padding(top = COMMON_PADDING),
                activePage = activePage,
                lastPage = lastPage,
                pageSizeList = PreviewPageViewModel.PageSizeList,
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

private fun updateCategoryLink(topCate: TopCate, subCate: SubCate?): String {
    val sb = StringBuilder("https://www.zcool.com.cn/discover?")
    sb.append("cate=").append(topCate.id)
    subCate?.id?.let { subCateId ->
        sb.append("&subCate=").append(subCateId)
    }
    return sb.toString()
}
