package com.rndeep.fns_fantoo.ui.menu.fantooclub.detail

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import timber.log.Timber

class VideoInfoViewModel : ViewModel() {

    private var player: ExoPlayer? = null
    private val playbackStateListener: Player.Listener = playbackStateListener()

    private var loadUri: String? = null
    private var playbackPosition = 0L

    private var playWhenReady = true
    private var currentItem = 0

    fun setLoadUri(uri: String?) {
        loadUri = uri
    }

    fun setPlaybackPosition(position: Long) {
        playbackPosition = position
    }

    fun initVideoInfo() {
        playbackPosition = 0L
        playWhenReady = true
    }

    fun getExoPlayer() = player

    fun getLoadUri() = loadUri

    fun getPlaybackPosition() = playbackPosition

    fun initializePlayer(context: Context, exoPlayerView: StyledPlayerView) {
        Timber.d("initializePlayer, uri: $loadUri, position: $playbackPosition")
        player = ExoPlayer.Builder(context)
            .build()
            .also { exoPlayer ->
                exoPlayerView.player = exoPlayer

                val mediaItem = MediaItem.Builder()
                    .setUri(loadUri)
                    .build()

                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }
    }

    fun releasePlayer() {
        player?.let { exoPlayer ->
            setPlaybackPosition(exoPlayer.currentPosition)
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
        player = null
    }

    private fun playbackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Timber.d("changed exoplayer state to $stateString")
        }
    }
}