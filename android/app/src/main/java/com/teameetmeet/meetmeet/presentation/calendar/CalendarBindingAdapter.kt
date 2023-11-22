package com.teameetmeet.meetmeet.presentation.calendar

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.util.toYearMonth
import java.time.LocalDate

@BindingAdapter("local_date")
fun TextView.bindLocalDate( localDate: LocalDate) {
    text = localDate.toYearMonth()
}

@BindingAdapter("day_list", "vm")
fun RecyclerView.bindDayInMonth(
    dayList: List<CalendarItem>,
    viewModel: CalendarViewModel
) {
    if (adapter == null) {
        adapter = CalendarAdapter(viewModel)
    }
    (adapter as CalendarAdapter).submitList(dayList)
}
