package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import com.teameetmeet.meetmeet.presentation.model.FeedMedia

interface MediaItemCancelClickListener {
    fun onItemClick(feedMedia: FeedMedia)
}