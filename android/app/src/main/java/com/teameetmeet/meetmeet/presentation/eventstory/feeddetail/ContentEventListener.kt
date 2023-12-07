package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource

interface ContentEventListener {
    fun onClick()
    fun getPlayer(position: Int): ExoPlayer
    fun getMediaSource(path: String): MediaSource
}