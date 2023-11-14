package com.teameetmeet.meetmeet.presentation.calendar

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.util.getDayListInMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class CalendarViewModel : ViewModel(), OnCalendarItemClickListener {

    private val _currentDate =
        MutableStateFlow<CalendarItem>(CalendarItem(date = LocalDate.now(), isSelected = true))
    val currentDate: StateFlow<CalendarItem> = _currentDate.asStateFlow()
    private val _daysInMonth =
        MutableStateFlow<List<CalendarItem>>(currentDate.value.date!!.getDayListInMonth(currentDate.value))
    val daysInMonth: StateFlow<List<CalendarItem>> = _daysInMonth.asStateFlow()

    fun forwardMonth() {
        _currentDate.update {
            it.copy(date = currentDate.value.date!!.minusMonths(1))
        }
        _daysInMonth.update {
            currentDate.value.date!!.getDayListInMonth(currentDate.value)
        }
    }

    fun backwardMonth() {
        _currentDate.update {
            it.copy(date = currentDate.value.date!!.plusMonths(1))
        }
        _daysInMonth.update {
            currentDate.value.date!!.getDayListInMonth(currentDate.value)
        }
    }

    override fun onItemClick(calendarItem: CalendarItem) {
        calendarItem.date ?: return
        if (currentDate.value != calendarItem) {
            _daysInMonth.update { list ->
                list.map {
                    when (it) {
                        currentDate.value -> {
                            it.copy(isSelected = false)
                        }

                        calendarItem -> {
                            it.copy(isSelected = true)
                        }

                        else -> {
                            it
                        }
                    }
                }
            }
            _currentDate.update {
                it.copy(date = calendarItem.date)
            }
        }
    }
}