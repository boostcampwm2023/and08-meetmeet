package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import com.teameetmeet.meetmeet.data.model.Feed

interface OnFeedItemClickListener {

    fun onItemClick(feed: Feed)
}