package com.teameetmeet.meetmeet.data.datasource

import com.teameetmeet.meetmeet.data.network.api.CalendarApi
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RemoteCalendarDataSource @Inject constructor(private val api: CalendarApi) {
    fun getEvents(startDate: String, endDate: String): Flow<List<EventResponse>> {
        return flowOf(true)
            .map { api.getEvents(startDate, endDate) }
            .catch {
                //todo: 예외 처리
            }
    }

    fun searchEvents(
        keyword: String?,
        startDate: String,
        endDate: String
    ): Flow<List<EventResponse>> {
        return flowOf(true)
            .map { api.getEvents(startDate, endDate) }
            .catch {
                //todo: 예외 처리
            }
    }
}