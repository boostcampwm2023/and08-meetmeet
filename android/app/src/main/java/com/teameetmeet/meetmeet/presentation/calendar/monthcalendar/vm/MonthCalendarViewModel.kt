package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.CalendarItemClickListener
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.DayClickEvent
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.presentation.model.EventBar
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.presentation.util.THROTTLE_DURATION
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
import com.teameetmeet.meetmeet.util.date.getLocalDate
import com.teameetmeet.meetmeet.util.date.toEndLong
import com.teameetmeet.meetmeet.util.date.toLocalDate
import com.teameetmeet.meetmeet.util.date.toStartLong
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

abstract class MonthCalendarViewModel : ViewModel(), CalendarItemClickListener {

    private val _currentDate = MutableStateFlow<CalendarItem>(CalendarItem(getLocalDate()))
    val currentDate: StateFlow<CalendarItem> = _currentDate

    private val _daysInMonth = MutableStateFlow<List<CalendarItem>>(
        currentDate.value.date?.let { CalendarItem.getListFrom(it) } ?: emptyList()
    )
    val daysInMonth: StateFlow<List<CalendarItem>> = _daysInMonth

    private val _dayClickEvent = MutableSharedFlow<CalendarItem>()

    private val _showBottomSheetEvent = MutableSharedFlow<DayClickEvent>()

    val showBottomSheetEvent: SharedFlow<DayClickEvent> = _showBottomSheetEvent.asSharedFlow()

    init {
        setDayClickRequestFlow()
    }

    abstract val isAddButtonVisible: Boolean

    abstract fun fetchEvents()

    protected fun setDaysInMonth(monthlyEvents: List<EventSimple>) {
        _daysInMonth.update {
            allocateEventsPerDay(it, monthlyEvents)
        }
        _currentDate.update { prev ->
            _daysInMonth.value.find { it.date == prev.date } ?: prev
        }
    }

    val comparator: (LocalDate) -> Comparator<EventSimple> = { today ->
        Comparator { event1, event2 ->
            val endToday1 = event1.endDateTime.toLocalDate() == today
            val endToday2 = event2.endDateTime.toLocalDate() == today

            if (endToday1 == endToday2) event1.startDateTime.compareTo(event2.startDateTime)
            else endToday1.compareTo(endToday2)
        }
    }

    private fun allocateEventsPerDay(
        calendarItems: List<CalendarItem>,
        monthlyEvents: List<EventSimple>
    ): List<CalendarItem> {
        val daysInMonth = mutableListOf<CalendarItem>()

        for (i in calendarItems.indices) {
            daysInMonth.add(
                CalendarItem(
                    date = calendarItems[i].date,
                    isSelected = calendarItems[i].isSelected
                )
            )
            val today = daysInMonth[i].date
            today ?: continue

            val todayEvents = monthlyEvents
                .filter {
                    it.startDateTime <= today.toEndLong() && it.endDateTime >= today.toStartLong()
                }.sortedBy { it.startDateTime }

            if (todayEvents.isEmpty()) continue

            val continuity = todayEvents
                .groupBy { event ->
                    i % 7 != 0 && today.dayOfMonth != 1 &&
                            daysInMonth[i - 1].eventBars.any { it?.id == event.id }
                }

            val eventBars: MutableList<EventBar?> = (0..<5).map { null }.toMutableList()

            continuity[true]?.map { event ->
                val index = daysInMonth[i - 1].eventBars.indexOfFirst { it?.id == event.id }
                eventBars[index] = EventBar(
                    id = event.id,
                    color = event.color,
                    isStart = false,
                    isEnd = i % 7 == 6 || i == calendarItems.lastIndex
                            || event.endDateTime.toLocalDate() == today
                )
            }

            continuity[false]
                ?.sortedWith(comparator(today))
                ?.map { event ->
                    val index = eventBars.indexOf(null)
                    val eventBar = EventBar(
                        id = event.id,
                        color = event.color,
                        isStart = true,
                        isEnd = i % 7 == 6 || i == calendarItems.lastIndex
                                || event.endDateTime.toLocalDate() == today
                    )
                    if (index != -1) eventBars[index] = eventBar else eventBars.add(eventBar)
                }

            if (eventBars.size > 5) {
                eventBars[4] = EventBar(
                    id = -1, isStart = true, isEnd = true, hiddenCount = eventBars.size - 4
                )
                if (i % 7 != 0 && today.dayOfMonth != 1) {
                    daysInMonth[i - 1] = daysInMonth[i - 1].copy(
                        eventBars = daysInMonth[i - 1].eventBars
                            .mapIndexed { index, eventBar ->
                                if (index == 4) eventBar?.copy(isEnd = true)
                                else eventBar
                            }
                    )
                }
            }

            daysInMonth[i] =
                daysInMonth[i].copy(events = todayEvents, eventBars = eventBars.take(5))
        }
        return daysInMonth
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

    override fun onItemClick(calendarItem: CalendarItem) {
        calendarItem.date ?: return
        if (calendarItem.events.isNotEmpty()) {
            viewModelScope.launch {
                _dayClickEvent.emit(calendarItem)
            }
        }
        if (currentDate.value.date == calendarItem.date) return
        _daysInMonth.update { list ->
            list.map {
                when (it.date) {
                    calendarItem.date -> it.copy(isSelected = true)
                    else -> it.copy(isSelected = false)
                }
            }
        }
        _currentDate.update { calendarItem }
    }

    private fun setDayClickRequestFlow() {
        _dayClickEvent.setClickEvent(viewModelScope, THROTTLE_DURATION) { calendarItem ->
            calendarItem.date?.let {
                _showBottomSheetEvent.emit(DayClickEvent(it, calendarItem.events))
            }
        }
    }
}