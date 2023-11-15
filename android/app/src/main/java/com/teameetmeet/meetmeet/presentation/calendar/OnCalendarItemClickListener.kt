package com.teameetmeet.meetmeet.presentation.calendar

import com.teameetmeet.meetmeet.presentation.model.CalendarItem

interface OnCalendarItemClickListener {

    fun onItemClick(calendarItem: CalendarItem)
}