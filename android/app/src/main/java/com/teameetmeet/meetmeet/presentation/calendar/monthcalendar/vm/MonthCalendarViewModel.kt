package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.DayClickEvent
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.presentation.util.THROTTLE_DURATION
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
import com.teameetmeet.meetmeet.util.date.getLocalDate
import com.teameetmeet.meetmeet.util.date.toEndLong
import com.teameetmeet.meetmeet.util.date.toStartLong
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MonthCalendarViewModel : ViewModel() {

    private val _currentDate = MutableStateFlow(CalendarItem(getLocalDate()))
    val currentDate: StateFlow<CalendarItem> = _currentDate

    private val _daysInMonth = MutableStateFlow(
        currentDate.value.date?.let { CalendarItem.getListFrom(it) } ?: emptyList()
    )
    val daysInMonth: StateFlow<List<CalendarItem>> = _daysInMonth

    private val _monthlyEvents = MutableStateFlow<List<EventSimple>>(emptyList())
    val monthlyEvents: StateFlow<List<EventSimple>> = _monthlyEvents

    private val _dayClickEvent = MutableSharedFlow<CalendarItem>()

    private val _showBottomSheetEvent = MutableSharedFlow<DayClickEvent>()

    val showBottomSheetEvent: SharedFlow<DayClickEvent> = _showBottomSheetEvent.asSharedFlow()

    init {
        setDayClickRequestFlow()
    }

    abstract val isAddButtonVisible: Boolean

    abstract fun fetchEvents()

    protected fun setDaysInMonth(monthlyEvents: List<EventSimple>) {
        _monthlyEvents.update { monthlyEvents }
        _daysInMonth.update { allocateEventsPerDay(it, monthlyEvents) }
        _currentDate.update { prev -> _daysInMonth.value.find { it.date == prev.date } ?: prev }
    }

    private fun allocateEventsPerDay(
        calendarItems: List<CalendarItem>,
        monthlyEvents: List<EventSimple>
    ): List<CalendarItem> {
        return calendarItems.map { day ->
            if (day.date == null) day
            else day.copy(
                events = monthlyEvents
                    .filter { event ->
                        event.startDateTime <= day.date.toEndLong() &&
                                event.endDateTime >= day.date.toStartLong()
                    }.sortedBy { it.startDateTime }
            )
        }
    }

    fun moveMonth(offset: Long) {
        _currentDate.update {
            CalendarItem(it.date?.plusMonths(offset))
        }
        _daysInMonth.update {
            currentDate.value.date?.let { CalendarItem.getListFrom(it) } ?: emptyList()
        }
        fetchEvents()
    }

    fun selectDay(index: Int) {
        if (index >= daysInMonth.value.size) return
        val selectedDate = daysInMonth.value[index]
        selectedDate.date ?: return
        if (selectedDate.events.isNotEmpty()) {
            viewModelScope.launch {
                _dayClickEvent.emit(selectedDate)
            }
        }
        if (currentDate.value.date == selectedDate.date) return
        _daysInMonth.update { list ->
            list.map {
                when (it.date) {
                    selectedDate.date -> it.copy(isSelected = true)
                    else -> it.copy(isSelected = false)
                }
            }
        }
        _currentDate.update { selectedDate }
    }

    private fun setDayClickRequestFlow() {
        _dayClickEvent.setClickEvent(viewModelScope, THROTTLE_DURATION) { calendarItem ->
            calendarItem.date?.let {
                _showBottomSheetEvent.emit(DayClickEvent(it, calendarItem.events))
            }
        }
    }
}