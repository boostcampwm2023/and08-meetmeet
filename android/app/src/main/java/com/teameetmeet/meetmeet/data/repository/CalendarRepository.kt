package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.toDateString
import com.teameetmeet.meetmeet.data.toEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val localCalendarDataSource: LocalCalendarDataSource,
    private val remoteCalendarDataSource: RemoteCalendarDataSource
) {
    suspend fun getEvents(startDate: Long, endDate: Long): Flow<List<Event>> {
        try {
            syncEvents(startDate, endDate)
        } finally {
            return localCalendarDataSource.getEvents(startDate, endDate)
        }
    }

    fun searchEvents(
        keyword: String? = null,
        startDate: String,
        endDate: String
    ): Flow<List<EventResponse>> {
        return remoteCalendarDataSource.searchEvents(keyword, startDate, endDate)
    }

    private suspend fun syncEvents(startDate: Long, endDate: Long) {
        //todo: edit distance 적용
        val local = localCalendarDataSource
            .getEvents(startDate, endDate)
            .first()
        val remote = remoteCalendarDataSource
            .getEvents(startDate.toDateString(), endDate.toDateString())
            .first()

        syncDeletes(local, remote)
        syncInserts(local, remote)
        syncUpdates(local, remote)
    }

    private suspend fun syncInserts(localEvents: List<Event>, remoteEvents: List<EventResponse>) {
        remoteEvents.filter { remoteEvent ->
            localEvents.none { localEvent -> localEvent.id == remoteEvent.id }
        }.mapNotNull { remoteEvent ->
            remoteEvent.toEvent()
        }.forEach { localEvent ->
            localCalendarDataSource.insert(localEvent)
        }
    }

    private suspend fun syncDeletes(localEvents: List<Event>, remoteEvents: List<EventResponse>) {
        localEvents.filter { localEvent ->
            remoteEvents.none { remoteEvent -> remoteEvent.id == localEvent.id }
        }.forEach { localEvent ->
            localCalendarDataSource.delete(localEvent)
        }
    }

    private suspend fun syncUpdates(localEvents: List<Event>, remoteEvents: List<EventResponse>) {
        remoteEvents
            .filter { remoteEvent ->
                localEvents.any { localEvent -> localEvent.id == remoteEvent.id }
            }
            .forEach { remoteEvent ->
                localCalendarDataSource.updateEventAttr(
                    id = remoteEvent.id,
                    title = remoteEvent.title,
                    startDate = remoteEvent.startDate,
                    endDate = remoteEvent.endDate,
                )
            }
    }
}