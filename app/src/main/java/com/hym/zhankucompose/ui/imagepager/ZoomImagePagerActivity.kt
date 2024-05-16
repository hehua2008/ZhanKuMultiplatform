package com.hym.zhankucompose.ui.imagepager

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.hym.compose.utils.detectTapWithoutConsume
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.R
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.ui.theme.ComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ZoomImagePagerActivity : BaseActivity() {
    companion object {
        private const val TAG = "ZoomImagePagerActivity"

        const val PHOTO_INFOS = "PHOTO_INFOS"
        const val CURRENT_POSITION = "CURRENT_POSITION"
        const val SCREEN_LOCATION = "SCREEN_LOCATION"
    }

    private var currentPosition = 0

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish(currentPosition)
            }
        })

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        val photoInfos: List<UrlPhotoInfo> = intent.getParcelableArrayListExtra(PHOTO_INFOS)!!
        currentPosition = intent.getIntExtra(CURRENT_POSITION, 0)

        showSystemBars(true)

        setContent {
            ComposeTheme {
                val density = LocalDensity.current
                val systemBarsTop = WindowInsets.systemBars.getTop(density)
                val topAppBarHeight = remember(density, systemBarsTop) {
                    with(density) { systemBarsTop.toDp() } + 36.dp
                }
                val photoInfoList = remember(photoInfos) { photoInfos.toImmutableList() }
                val pagerState = rememberPagerState(initialPage = currentPosition) {
                    photoInfoList.size
                }
                var showAppBar by remember { mutableStateOf(true) }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }
                        .collectLatest {
                            currentPosition = it
                        }
                }

                LaunchedEffect(null) {
                    snapshotFlow { showAppBar }
                        .collectLatest {
                            showSystemBars(it)
                        }
                }

                Box {
                    ImageHorizontalPager(
                        photoInfoList = photoInfoList,
                        initialIndex = currentPosition,
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
                                        .clickable { finish(pagerState.currentPage) }
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
    }

    private fun finish(currentPosition: Int) {
        val screenLocation = window.decorView.run {
            IntArray(2).let {
                getLocationOnScreen(it)
                Rect(it[0], it[1], it[0] + width, it[1] + height)
            }
        }
        val data = Intent()
            .putExtra(CURRENT_POSITION, currentPosition)
            .putExtra(SCREEN_LOCATION, screenLocation)
        setResult(RESULT_OK, data)

        finish()
        overridePendingTransition(0, android.R.anim.fade_out)
    }

    private fun showSystemBars(show: Boolean) {
        WindowCompat.getInsetsController(window, window.decorView).run {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (show) show(WindowInsetsCompat.Type.systemBars())
            else hide(WindowInsetsCompat.Type.systemBars())
        }
    }
}
