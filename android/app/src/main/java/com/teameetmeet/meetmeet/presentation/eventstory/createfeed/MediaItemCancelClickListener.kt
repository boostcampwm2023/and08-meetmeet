package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import com.teameetmeet.meetmeet.presentation.model.MediaItem

interface MediaItemCancelClickListener {
    fun onItemClick(mediaItem: MediaItem)
}