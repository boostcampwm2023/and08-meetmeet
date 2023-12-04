package com.teameetmeet.meetmeet.presentation.calendar

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.teameetmeet.meetmeet.util.date.toYearMonth
import java.time.LocalDate

@BindingAdapter("local_date")
fun TextView.bindLocalDate(localDate: LocalDate) {
    text = localDate.toYearMonth()
}