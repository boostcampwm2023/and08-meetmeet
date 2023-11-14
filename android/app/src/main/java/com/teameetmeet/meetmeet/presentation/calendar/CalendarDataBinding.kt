package com.teameetmeet.meetmeet.presentation.calendar

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.util.getDayListInMonth
import com.teameetmeet.meetmeet.util.toYearMonth
import java.time.LocalDate

@BindingAdapter("local_date")
fun bindLocalDate(textView: TextView, localDate: LocalDate) {
    textView.text = localDate.toYearMonth()
}

@BindingAdapter("local_date")
fun bindDayInMonth(recyclerView: RecyclerView, localDate: LocalDate) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = CalendarAdapter()
    }
    (recyclerView.adapter as CalendarAdapter).submitList(localDate.getDayListInMonth())
}