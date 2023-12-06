package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm

import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.presentation.model.toEventSimple
import com.teameetmeet.meetmeet.util.date.toEndLong
import com.teameetmeet.meetmeet.util.date.toStartLong
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OthersMonthCalendarViewModel(
    private val calendarRepository: CalendarRepository,
    private val userId: Int
) : MonthCalendarViewModel() {
    override val isAddButtonVisible: Boolean = false

    override fun fetchEvents() {
        viewModelScope.launch {
            val date = currentDate.value.date ?: return@launch
            val startDateTime = date.withDayOfMonth(1).toStartLong()
            val endDateTime = date.withDayOfMonth(date.lengthOfMonth()).toEndLong()
            calendarRepository
                .getEventsByUserId(userId, startDateTime, endDateTime)
                .collectLatest {
                    setDaysInMonth(it.map(Event::toEventSimple))
                }
        }
    }

    companion object {
        const val TYPE = "other"
    }
}