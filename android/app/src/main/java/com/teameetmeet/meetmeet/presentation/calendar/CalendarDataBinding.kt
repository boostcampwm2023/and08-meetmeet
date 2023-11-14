package com.teameetmeet.meetmeet.presentation.calendar

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.teameetmeet.meetmeet.util.toYearMonth
import java.time.LocalDate

@BindingAdapter("local_date")
fun bindLocalDate(textView: TextView, localDate: LocalDate) {
    textView.text = localDate.toYearMonth()
}