package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.RadioGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import com.teameetmeet.meetmeet.presentation.model.EventTime

@BindingAdapter("event_time")
fun TextView.bindEventTime(eventTime: EventTime) {
    text = eventTime.toString()
}

@BindingAdapter("menu_enable")
fun MaterialToolbar.bindMenuEnable(eventAuthority: EventAuthority) {
    menu.findItem(R.id.menu_save).isVisible = eventAuthority != EventAuthority.GUEST
}

@BindingAdapter("repeat_frequency")
fun AutoCompleteTextView.bindRepeatFrequency(repeatFrequency: Int?) {
    if(repeatFrequency!=null) {
        setText(repeatFrequency.toString())
    }
    val frequencyItems = arrayOf("1", "2", "3", "4", "5", "6")
    setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, frequencyItems))
}

@BindingAdapter("repeat_term")
fun AutoCompleteTextView.bindRepeatTerm(repeatTerm: EventRepeatTerm) {
    setText(repeatTerm.stringResId)
    val items = EventRepeatTerm.entries.map { context.getString(it.stringResId) }.toTypedArray()
    setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
}

@BindingAdapter("notification")
fun AutoCompleteTextView.bindNotification(notification: EventNotification) {
    setText(notification.stringResId)
    val items = EventNotification.entries.map { context.getString(it.stringResId) }.toTypedArray()
    setAdapter(ArrayAdapter(context, android.R.layout.simple_list_item_1, items))
}

@BindingAdapter("color")
fun RadioGroup.bindColor(eventColor: EventColor) {
    check(
        when (eventColor) {
            EventColor.RED -> R.id.radio_red
            EventColor.BLUE -> R.id.radio_blue
            EventColor.GREEN -> R.id.radio_green
            EventColor.ORANGE -> R.id.radio_orange
            EventColor.PURPLE -> R.id.radio_purple
        }
    )
}