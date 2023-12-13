package com.teameetmeet.meetmeet.data.datasource

import com.teameetmeet.meetmeet.data.network.api.CalendarApi
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.network.entity.UserEventResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RemoteCalendarDataSource @Inject constructor(private val api: CalendarApi) {
    fun getEvents(startDate: String, endDate: String): Flow<List<EventResponse>> {
        return flowOf(true).map { api.getEvents(startDate, endDate).events }
    }

    fun getEventsByUserId(
        userId: Int,
        startDate: String,
        endDate: String
    ): Flow<List<UserEventResponse>> {
        return flowOf(true).map { api.getEventsByUserId(userId, startDate, endDate).events }
    }

    fun searchEvents(
        keyword: String?,
        startDate: String,
        endDate: String
    ): Flow<List<EventResponse>> {
        return flowOf(true).map { api.searchEvents(keyword, startDate, endDate).events }
    }

    fun addEvent(addEventRequest: AddEventRequest): Flow<List<EventResponse>> {
        return flowOf(true).map { api.addEvent(addEventRequest).events }
    }
}