package com.hym.zhankucompose.ui.imagepager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hym.compose.utils.detectTapWithoutConsume
import com.hym.zhankucompose.R
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.ui.theme.ComposeTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.collectLatest

/**
 * @author hehua2008
 * @date 2024/6/29
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ZoomImagePagerScreen(
    photoInfoList: ImmutableList<UrlPhotoInfo>,
    initialIndex: Int
) {
    ComposeTheme {
        val navController = LocalNavController.current
        val density = LocalDensity.current
        val systemBarsTop = WindowInsets.systemBars.getTop(density)
        val topAppBarHeight = remember(density, systemBarsTop) {
            with(density) { systemBarsTop.toDp() } + 36.dp
        }
        val pagerState = rememberPagerState(initialPage = initialIndex) {
            photoInfoList.size
        }
        var currentIndex by remember { mutableIntStateOf(initialIndex) }
        var showAppBar by remember { mutableStateOf(true) }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }
                .collectLatest {
                    currentIndex = it
                }
        }

        LaunchedEffect(null) {
            snapshotFlow { showAppBar }
                .collectLatest {
                    // TODO: showSystemBars(it)
                }
        }

        Box {
            ImageHorizontalPager(
                photoInfoList = photoInfoList,
                initialIndex = initialIndex,
                pagerState = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(null) {
                        detectTapWithoutConsume {
                            showAppBar = !showAppBar
                        }
                    }
            )

            AnimatedVisibility(
                visible = showAppBar,
                modifier = Modifier.height(topAppBarHeight),
                enter = remember { fadeIn() },
                exit = remember { fadeOut() }
            ) {
                TopAppBar(
                    modifier = Modifier.fillMaxHeight(),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${pagerState.currentPage + 1} / ${pagerState.pageCount}",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.vector_arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    navController.popBackStack()
                                }
                                .fillMaxHeight()
                                .padding(horizontal = 12.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            }
        }
    }
}
