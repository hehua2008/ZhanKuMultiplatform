package com.hym.zhankumultiplatform.ui.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import com.hym.zhankumultiplatform.player.PlayerProvider

/**
 * @author hehua2008
 * @date 2024/3/17
 */
@Composable
expect fun DetailContentVideo(
    detailVideo: DetailVideo,
    modifier: Modifier = Modifier,
    playPainter: Painter,
    pausePainter: Painter,
    replayPainter: Painter,
    playerProvider: PlayerProvider,
    onVideoPlayFailed: (detailVideo: DetailVideo) -> Unit,
    size: IntSize? = null,
    onGetSize: ((size: IntSize) -> Unit)? = null,
    playPosition: Long?,
    savePlayPosition: (detailVideo: DetailVideo, position: Long) -> Unit,
)
