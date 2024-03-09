package com.hym.zhankucompose.ui.main

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.times
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.model.Content
import com.hym.zhankucompose.ui.NetworkStateLayout
import com.hym.zhankucompose.ui.author.AuthorItemFragment
import com.hym.zhankucompose.ui.detail.DetailActivity
import com.hym.zhankucompose.ui.tag.TagActivity
import com.hym.zhankucompose.util.getActivity

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
    modifier: Modifier = Modifier,
    columnSize: Int = 1,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior()
) {
    val view = LocalView.current
    val activity = remember(view) { view.getActivity() }
    val surfaceContainerColor = MaterialTheme.colorScheme.surfaceContainer
    val horizontalArrangement = remember(columnSize) {
        object : Arrangement.Horizontal by Arrangement.SpaceEvenly {
            override val spacing = (columnSize + 1) * COMMON_PADDING
        }
    }
    val viewsPainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_eye)
    )
    val commentPainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_comment)
    )
    val favoritePainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_favorite)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(columnSize),
        modifier = modifier,
        state = lazyGridState,
        flingBehavior = flingBehavior,
        verticalArrangement = VerticalArrangement,
        horizontalArrangement = horizontalArrangement
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.id }
        ) { index ->
            val previewItem = lazyPagingItems[index] // Note that item may be null.
            if (previewItem == null) {
                Log.e(TAG, "previewItem of index[$index] is null!")
                return@items
            }
            Surface(color = surfaceContainerColor, shape = ShapeDefaults.Small) {
                PreviewItem(
                    content = previewItem,
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    viewsPainter = viewsPainter,
                    commentPainter = commentPainter,
                    favoritePainter = favoritePainter,
                    onImageClick = {
                        activity?.let {
                            val intent = Intent(it, DetailActivity::class.java)
                                .putExtra(DetailActivity.KEY_TITLE, previewItem.formatTitle)
                                .putExtra(DetailActivity.KEY_CONTENT_TYPE, previewItem.objectType)
                                .putExtra(DetailActivity.KEY_CONTENT_ID, previewItem.contentId)
                            it.startActivity(intent)
                        }
                    },
                    onAuthorClick = {
                        val context = view.context
                        val intent = Intent(context, TagActivity::class.java)
                            .putExtra(AuthorItemFragment.AUTHOR, previewItem.creatorObj)
                        context.startActivity(intent)
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
