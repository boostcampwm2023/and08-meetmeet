package com.teameetmeet.meetmeet.presentation.calendar

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.util.toYearMonth
import java.time.LocalDate

@BindingAdapter("local_date")
fun bindLocalDate(textView: TextView, localDate: LocalDate) {
    textView.text = localDate.toYearMonth()
}

@BindingAdapter("day_list", "vm")
fun bindDayInMonth(
    recyclerView: RecyclerView,
    dayList: List<CalendarItem>,
    viewModel: CalendarViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = CalendarAdapter(viewModel)
    }
    (recyclerView.adapter as CalendarAdapter).submitList(dayList)
}