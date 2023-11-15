package com.teameetmeet.meetmeet.presentation.addevent

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@BindingAdapter("long_date_text")
fun TextView.bindLongDateText(date: Long) {
    this.text = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date(date))
}