package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import androidx.media3.exoplayer.ExoPlayer

interface ContentEventListener {
    fun onClick()
    fun onVideoPrepared(player: ExoPlayer, position: Int)
}