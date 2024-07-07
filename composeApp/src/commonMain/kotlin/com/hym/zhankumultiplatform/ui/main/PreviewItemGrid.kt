package com.hym.zhankumultiplatform.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.times
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.hym.zhankumultiplatform.compose.COMMON_PADDING
import com.hym.zhankumultiplatform.model.Content
import com.hym.zhankumultiplatform.model.ContentType
import com.hym.zhankumultiplatform.navigation.DetailsArgs
import com.hym.zhankumultiplatform.navigation.LocalNavListener
import com.hym.zhankumultiplatform.navigation.TagListArgs
import com.hym.zhankumultiplatform.ui.NetworkStateLayout
import com.hym.zhankumultiplatform.util.Logger
import org.jetbrains.compose.resources.vectorResource
import zhankumultiplatform.composeapp.generated.resources.Res
import zhankumultiplatform.composeapp.generated.resources.vector_comment
import zhankumultiplatform.composeapp.generated.resources.vector_eye

/**
 * @author hehua2008
 * @date 2024/3/9
 */
private const val TAG = "PreviewItemGrid"

private val VerticalArrangement = Arrangement.spacedBy(COMMON_PADDING)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreviewItemGrid(
    lazyPagingItems: LazyPagingItems<Content>,
    showSnackbar: suspend (message: String, actionLabel: String?, withDismiss: Boolean, duration: SnackbarDuration) -> SnackbarResult,
    modifier: Modifier = Modifier,
    columnSize: Int = 1,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    headerContent: @Composable ((headerModifier: Modifier) -> Unit)? = null
) {
    val navListener = LocalNavListener.current
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer
    val horizontalArrangement = remember(columnSize) {
        object : Arrangement.Horizontal by Arrangement.SpaceEvenly {
            override val spacing = (columnSize + 1) * COMMON_PADDING
        }
    }
    val viewsPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_eye)
    )
    val commentPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_comment)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnSize),
        modifier = modifier,
        state = lazyGridState,
        flingBehavior = flingBehavior,
        verticalArrangement = VerticalArrangement,
        horizontalArrangement = horizontalArrangement
    ) {
        if (headerContent != null) {
            item(key = "HeaderContent", span = { GridItemSpan(maxLineSpan) }) {
                headerContent(Modifier.animateItemPlacement())
            }
        }

        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.id }
        ) { index ->
            val previewItem = lazyPagingItems[index] // Note that item may be null.
            if (previewItem == null) {
                Logger.e(TAG, "previewItem of index[$index] is null!")
                return@items
            }
            Surface(color = surfaceContainerColor, shape = ShapeDefaults.Small) {
                PreviewItem(
                    content = previewItem,
                    showSnackbar = showSnackbar,
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    viewsPainter = viewsPainter,
                    commentPainter = commentPainter,
                    onImageClick = {
                        val contentType = ContentType.entries.firstOrNull { type ->
                            type.value == previewItem.objectType
                        } ?: ContentType.WORK
                        navListener.onNavigateToDetails(
                            DetailsArgs(contentType, previewItem.contentId)
                        )
                    },
                    onAuthorClick = {
                        navListener.onNavigateToTagList(
                            TagListArgs(previewItem.creatorObj, null, null)
                        )
                    }
                )
            }
        }

        val appendLoadState = lazyPagingItems.loadState.append
        if (appendLoadState !is LoadState.NotLoading) {
            item(key = "NetworkStateLayout") {
                NetworkStateLayout(
                    loadState = appendLoadState,
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    lazyPagingItems.retry()
                }
            }
        }
    }
}
