package com.teameetmeet.meetmeet.presentation.searchevent.searchevent

import com.teameetmeet.meetmeet.data.network.entity.EventResponse

interface SearchItemClickListener {
    fun onClick(event:EventResponse)
}