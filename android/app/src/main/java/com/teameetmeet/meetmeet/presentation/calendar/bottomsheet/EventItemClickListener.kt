package com.teameetmeet.meetmeet.presentation.calendar.bottomsheet

import com.teameetmeet.meetmeet.presentation.model.EventSimple

interface EventItemClickListener{
    fun onItemClick(eventSimple: EventSimple)
}