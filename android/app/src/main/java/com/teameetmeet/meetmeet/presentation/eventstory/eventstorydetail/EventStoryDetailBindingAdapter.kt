package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import com.teameetmeet.meetmeet.presentation.model.EventTime

@BindingAdapter("event_time")
fun TextView.bindEventTime(eventTime: EventTime) {
    text = eventTime.toString()
}

@BindingAdapter("menu_enable")
fun MaterialToolbar.bindMenuEnable(eventAuthority: EventAuthority){
    menu.findItem(R.id.menu_save).isVisible = eventAuthority != EventAuthority.GUEST
}