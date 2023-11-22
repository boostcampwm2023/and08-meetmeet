package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.teameetmeet.meetmeet.presentation.model.EventTime

@BindingAdapter("event_time")
fun TextView.bindEventTime(eventTime: EventTime) {
    text = eventTime.toString()
}