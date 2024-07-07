package com.hym.zhankumultiplatform.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.paging.LoadState
import com.hym.zhankumultiplatform.compose.EMPTY_BLOCK
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * @author hehua2008
 * @date 2024/3/9
 */
private val CircularProgressSize = 40.dp

@Composable
fun NetworkStateLayout(
    loadState: LoadState,
    modifier: Modifier = Modifier,
    onRetryClick: () -> Unit
) {
    ConstraintLayout(modifier = modifier.aspectRatio(1f)) {
        val (errorText, progressBar, retryButton) = createRefs()
        createVerticalChain(
            errorText,
            progressBar,
            retryButton,
            chainStyle = ChainStyle.Spread
        )

        val errorMsg = remember(loadState) {
            (loadState as? LoadState.Error)?.error?.message ?: ""
        }

        Text(
            text = errorMsg,
            modifier = Modifier
                //.wrapContentSize()
                .wrapContentWidth()
                .run {
                    if (errorMsg.isNotBlank()) wrapContentHeight() else height(0.dp)
                }
                .constrainAs(errorText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(progressBar.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    //visibility = if (errorMsg.isNotBlank()) Visibility.Visible else Visibility.Gone
                },
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )

        CircularProgressIndicator(
            modifier = Modifier
                //.size(CircularProgressSize)
                .run {
                    if (loadState is LoadState.Loading) size(CircularProgressSize) else size(0.dp)
                }
                .constrainAs(progressBar) {
                    top.linkTo(errorText.bottom)
                    bottom.linkTo(retryButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    //visibility =
                    //    if (loadState is LoadState.Loading) Visibility.Visible else Visibility.Gone
                }
        )

        Button(
            modifier = Modifier
                //.wrapContentSize()
                .wrapContentWidth()
                .run {
                    if (loadState is LoadState.Error) wrapContentHeight() else height(0.dp)
                }
                .constrainAs(retryButton) {
                    top.linkTo(progressBar.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    //visibility =
                    //    if (loadState is LoadState.Error) Visibility.Visible else Visibility.Gone
                },
            onClick = onRetryClick
        ) {
            Text("RETRY")
        }
    }
}

@Preview
@Composable
private fun PreviewNetworkStateLayout() {
    NetworkStateLayout(
        loadState = LoadState.Error(Exception("Exception")),
        modifier = Modifier.background(Color.White),
        onRetryClick = EMPTY_BLOCK
    )
}
