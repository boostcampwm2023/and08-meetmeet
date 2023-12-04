package com.teameetmeet.meetmeet.presentation.calendar

import com.teameetmeet.meetmeet.presentation.model.EventSimple
import java.io.Serializable
import java.time.LocalDate

data class DayClickEvent(
    val date: LocalDate,
    val events: List<EventSimple>
) : Serializable