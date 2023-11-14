package com.teameetmeet.meetmeet.data.datasource

import com.teameetmeet.meetmeet.data.network.api.ServerApi
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteCalendarDataSource(private val api: ServerApi) {
    suspend fun getEvents(startDate: String, endDate: String): Flow<List<EventResponse>> {
        return flow {
            //todo: 서버 예외 처리
            emit(api.getEvents(startDate, endDate))
        }
    }
}