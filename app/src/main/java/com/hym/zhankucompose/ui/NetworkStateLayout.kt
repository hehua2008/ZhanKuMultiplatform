package com.hym.zhankucompose.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Visibility
import androidx.paging.LoadState
import com.hym.zhankucompose.compose.EMPTY_BLOCK

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
                .wrapContentSize()
                .constrainAs(errorText) {
                    top.linkTo(parent.top)
                    bottom.linkTo(progressBar.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    visibility = if (errorMsg.isNotBlank()) Visibility.Visible else Visibility.Gone
                },
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )

        CircularProgressIndicator(
            modifier = Modifier
                .size(CircularProgressSize)
                .constrainAs(progressBar) {
                    top.linkTo(errorText.bottom)
                    bottom.linkTo(retryButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    visibility =
                        if (loadState is LoadState.Loading) Visibility.Visible else Visibility.Gone
                }
        )

        Button(
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(retryButton) {
                    top.linkTo(progressBar.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    visibility =
                        if (loadState is LoadState.Error) Visibility.Visible else Visibility.Gone
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
