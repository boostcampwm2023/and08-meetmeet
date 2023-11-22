package com.teameetmeet.meetmeet.data.datasource

import com.teameetmeet.meetmeet.data.local.database.dao.EventDao
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.toDateLong
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalCalendarDataSource @Inject constructor(private val dao: EventDao) {
    fun get(id: Int): Flow<Event> {
        return dao.get(id)
    }

    fun getEvents(startDate: Long, endDate: Long): Flow<List<Event>> {
        return dao.getEvents(startDate, endDate)
    }

    suspend fun insert(event: Event) {
        dao.insert(event)
    }

    suspend fun update(event: Event) {
        dao.update(event)
    }

    suspend fun delete(event: Event) {
        dao.delete(event)
    }

    suspend fun updateEventAttr(
        id: Int,
        title: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        color: String? = null,
        notification: String? = null
    ) {
        title?.let { title -> dao.updateTitle(id, title) }
        startDate?.toDateLong()?.let { startDateLong -> dao.updateStartDateTime(id, startDateLong) }
        endDate?.toDateLong()?.let { endDateLong -> dao.updateEndDateTime(id, endDateLong) }
        color?.let { color -> dao.updateTitle(id, color) }
        notification?.let { notification -> dao.updateTitle(id, notification) }
    }
}