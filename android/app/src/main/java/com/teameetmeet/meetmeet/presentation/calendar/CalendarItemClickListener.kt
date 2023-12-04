package com.teameetmeet.meetmeet.presentation.calendar

import com.teameetmeet.meetmeet.presentation.model.CalendarItem

interface CalendarItemClickListener {
    fun onItemClick(calendarItem: CalendarItem)
}