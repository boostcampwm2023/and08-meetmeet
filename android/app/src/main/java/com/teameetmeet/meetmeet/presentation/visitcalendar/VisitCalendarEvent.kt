package com.teameetmeet.meetmeet.presentation.visitcalendar

sealed class VisitCalendarEvent{
    data class NavigateToProfileImageFragment(val imageUrl: String?): VisitCalendarEvent()
}
