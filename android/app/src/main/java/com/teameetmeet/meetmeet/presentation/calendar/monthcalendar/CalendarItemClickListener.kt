package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import com.teameetmeet.meetmeet.presentation.model.CalendarItem

interface CalendarItemClickListener {
    fun onItemClick(calendarItem: CalendarItem)
}