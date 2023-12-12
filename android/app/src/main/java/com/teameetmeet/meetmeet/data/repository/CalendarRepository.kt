package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.network.entity.AddEventRequest
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.data.network.entity.UserEventResponse
import com.teameetmeet.meetmeet.data.toEvent
import com.teameetmeet.meetmeet.data.toException
import com.teameetmeet.meetmeet.presentation.model.EventColor
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.toDateString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.ZoneId
import javax.inject.Inject

class CalendarRepository @Inject constructor(
    private val localCalendarDataSource: LocalCalendarDataSource,
    private val remoteCalendarDataSource: RemoteCalendarDataSource
) {
    fun getSyncedEvents(startDateTime: Long, endDateTime: Long): Flow<List<Event>> {
        return remoteCalendarDataSource
            .getEvents(
                startDateTime.toDateString(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC")),
                endDateTime.toDateString(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
            ).onEach {
                localCalendarDataSource.deleteEvents(startDateTime, endDateTime)
                localCalendarDataSource.insertEvents(it.map(EventResponse::toEvent))
            }.map {
                localCalendarDataSource.getEvents(startDateTime, endDateTime).first()
            }.catch {
                emit(localCalendarDataSource.getEvents(startDateTime, endDateTime).first())
            }
    }

    fun getEventsByUserId(userId: Int, startDateTime: Long, endDateTime: Long): Flow<List<Event>> {
        return remoteCalendarDataSource
            .getEventsByUserId(
                userId,
                startDateTime.toDateString(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC")),
                endDateTime.toDateString(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
            ).map {
                it.map(UserEventResponse::toEvent)
            }.catch {
                throw it.toException()
            }
    }

    suspend fun deleteEvents() {
        localCalendarDataSource.deleteAll()
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
    ): Flow<List<EventResponse>> {
        val request = AddEventRequest(
            title = title,
            startDate = startDate,
            endDate = endDate,
            isJoinable = isJoinable,
            isVisible = isVisible,
            alarmMinutes = alarm.minutes,
            memo = memo.ifEmpty { null },
            color = color.value,
            repeatTerm = repeatTerm,
            repeatFrequency = repeatFrequency,
            repeatEndDate = repeatEndDate
        )
        return remoteCalendarDataSource.addEvent(request)
            .catch {
                throw it.toException()
            }
    }

    fun searchEvents(
        keyword: String?,
        startDate: String,
        endDate: String
    ): Flow<List<EventResponse>> {
        return remoteCalendarDataSource.searchEvents(keyword, startDate, endDate)
            .catch {
                throw it.toException()
            }
    }
}