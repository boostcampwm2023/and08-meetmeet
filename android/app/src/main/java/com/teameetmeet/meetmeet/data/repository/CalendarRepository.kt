package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.toEvent
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.util.DateTimeFormat
import com.teameetmeet.meetmeet.util.toDateString
import com.teameetmeet.meetmeet.util.toTimeStampLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import java.time.ZoneId
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

    fun addEvent(
        title: String,
        startDate: String,
        endDate: String,
        isJoinable: Boolean,
        isVisible: Boolean,
        memo: String,
        repeatTerm: String?,
        repeatFrequency: Int,
        repeatEndDate: String,
        color: EventColor,
        alarm: EventNotification
    ): Flow<Unit> {
        val request = AddEventRequest(
            title,
            startDate,
            endDate,
            isJoinable,
            isVisible,
            memo.ifEmpty { null },
            repeatTerm,
            repeatFrequency,
            repeatEndDate
        )
        return remoteCalendarDataSource.addEvent(request)
            .catch {
            }
    }

    fun searchEvents(
        keyword: String?,
        startDate: String,
        endDate: String
    ): Flow<List<EventResponse>> {
        return remoteCalendarDataSource.searchEvents(keyword, startDate, endDate)
    }

    private suspend fun syncEvents(startDateTime: Long, endDateTime: Long) {
        //todo: edit distance 적용
        val local = localCalendarDataSource
            .getEvents(startDateTime, endDateTime)
            .first()
        val remote = remoteCalendarDataSource
            .getEvents(
                startDateTime.toDateString(DateTimeFormat.SERVER_DATE, ZoneId.of("UTC")),
                endDateTime.toDateString(DateTimeFormat.SERVER_DATE, ZoneId.of("UTC"))
            )
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
                    startDateTime = remoteEvent.startDate.toTimeStampLong(
                        DateTimeFormat.ISO_DATE_TIME,
                        ZoneId.of("UTC")
                    ),
                    endDateTime = remoteEvent.endDate.toTimeStampLong(
                        DateTimeFormat.ISO_DATE_TIME,
                        ZoneId.of("UTC")
                    ),
                )
            }
    }
}