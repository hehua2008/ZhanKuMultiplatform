package com.hym.zhankucompose.ui.detail

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.ParserException
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.decoder.DecoderException
import androidx.media3.ui.PlayerView
import com.hym.zhankucompose.player.PlayerProvider
import com.hym.zhankucompose.player.playerView

/**
 * @author hehua2008
 * @date 2024/3/17
 */
@Composable
fun DetailContentVideo(
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
) {
    var isPlaying by remember(detailVideo) { mutableStateOf(false) }
    var isBuffering by remember(detailVideo) { mutableStateOf(false) }
    var isEnded by remember(detailVideo) { mutableStateOf(false) }
    var parseOrDecodeFailed by remember(detailVideo) { mutableStateOf(false) }
    var showComposeController by remember(detailVideo) { mutableStateOf(true) }
    var videoRatio by remember(size) {
        mutableFloatStateOf(size?.run { width / height.toFloat() } ?: (16 / 9f))
    }
    var player by remember { mutableStateOf<Player?>(null) }

    DisposableEffect(detailVideo) {
        val playerListener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
                if (playing) {
                    parseOrDecodeFailed = false
                    showComposeController = false
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = (playbackState == Player.STATE_BUFFERING)
                isEnded = (playbackState == Player.STATE_ENDED).also {
                    if (it) showComposeController = true
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                when (error.cause) {
                    is ParserException, is DecoderException -> parseOrDecodeFailed = true
                }
                showComposeController = true
            }

            override fun onVideoSizeChanged(videoSize: VideoSize) {
                if (videoSize.width == 0 || videoSize.height == 0) return
                videoRatio = videoSize.width / videoSize.height.toFloat()
                onGetSize?.invoke(IntSize(videoSize.width, videoSize.height))
            }
        }

        val mediaItem = MediaItem.Builder()
            .setUri(detailVideo.data.url)
            //.setMimeType(MimeTypes.APPLICATION_MP4)
            .build()

        player = playerProvider.obtain().apply {
            addListener(playerListener)
            setMediaItem(mediaItem)
            prepare()
            playPosition?.let { seekTo(it) }
        }

        onDispose {
            player?.let {
                savePlayPosition(detailVideo, it.currentPosition)
                it.removeListener(playerListener)
                it.playerView = null
                playerProvider.recycle(it)
            }
            // player = null // Do not do this!!!
        }
    }

    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                contentDescription = detailVideo.data.url
                setShowNextButton(false)
                setShowPreviousButton(false)
                setShowFastForwardButton(false)
                setShowRewindButton(false)
                controllerShowTimeoutMs = 1000
                setShutterBackgroundColor(android.graphics.Color.DKGRAY)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(videoRatio),
        onReset = { playerView -> playerView.player = null },
        onRelease = { playerView -> playerView.player = null },
        update = { playerView ->
            player?.playerView = playerView
            if (parseOrDecodeFailed) {
                playerView.setOnClickListener {
                    onVideoPlayFailed(detailVideo)
                }
                playerView.useController = false
            } else {
                playerView.setOnClickListener(null)
                playerView.useController = true
            }
        }
    )
}
