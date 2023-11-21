package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.Feed
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventFeedListAdapter
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventMemberListAdapter
import com.teameetmeet.meetmeet.util.convertDpToPx

@BindingAdapter("event_story")
fun TextView.bindTextViewDate(eventStory: EventStory?) {
    eventStory ?: return
    text = String.format(context.getString(R.string.event_story_event_date), eventStory.startDate, eventStory.endDate)
}