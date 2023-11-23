package com.teameetmeet.meetmeet.presentation.addevent

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("date_text")
fun TextView.bindLongDateText(date: LocalDateTime) {
    this.text = date.format(DateTimeFormatter.ISO_DATE)
}