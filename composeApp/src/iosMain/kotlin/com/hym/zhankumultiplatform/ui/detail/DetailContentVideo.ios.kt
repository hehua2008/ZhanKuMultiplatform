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
actual fun DetailContentVideo(
    detailVideo: DetailVideo,
    modifier: Modifier,
    playPainter: Painter,
    pausePainter: Painter,
    replayPainter: Painter,
    playerProvider: PlayerProvider,
    onVideoPlayFailed: (detailVideo: DetailVideo) -> Unit,
    size: IntSize?,
    onGetSize: ((size: IntSize) -> Unit)?,
    playPosition: Long?,
    savePlayPosition: (detailVideo: DetailVideo, position: Long) -> Unit
) {
    // TODO
}
