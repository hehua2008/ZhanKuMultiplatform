package com.hym.zhankucompose.ui.main

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.LabelFlowLayout
import com.hym.zhankucompose.compose.SMALL_PADDING_VALUES
import com.hym.zhankucompose.compose.SimpleLinkText
import com.hym.zhankucompose.compose.SimpleRadioGroup
import com.hym.zhankucompose.model.Cate
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.PagedLayout
import com.hym.zhankucompose.ui.webview.WebViewActivity
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
    pageViewModel: PreviewPageViewModel = viewModel(key = topCate.name)
) {
    LaunchedEffect(pageViewModel, topCate, initialSubCate) {
        pageViewModel.topCate = topCate
        pageViewModel.setSubCate(initialSubCate)
    }

    val context = LocalContext.current
    val lazyPagingItems = pageViewModel.pagingFlow.collectAsLazyPagingItems()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lazyPagingItems, lifecycleOwner) {
        val observer = Observer<Unit> { lazyPagingItems.refresh() }
        pageViewModel.mediatorLiveData.observe(lifecycleOwner, observer)

        onDispose {
            pageViewModel.mediatorLiveData.removeObserver(observer)
        }
    }

    val subCate by pageViewModel.subCate.observeAsState()
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

    val activePage by pageViewModel.page.observeAsState(1)
    val lastPage by pageViewModel.totalPages.observeAsState(1)
    val pageSizeIndex by pageViewModel.pageSizeIndex.observeAsState(0)

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
                val intent = Intent(context, WebViewActivity::class.java)
                    .putExtra(WebViewActivity.WEB_URL, it)
                    .putExtra(WebViewActivity.WEB_TITLE, (subCate ?: topCate).name)
                context.startActivity(intent)
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
