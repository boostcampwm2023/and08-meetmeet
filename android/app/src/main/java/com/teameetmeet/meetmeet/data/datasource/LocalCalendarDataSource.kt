package com.teameetmeet.meetmeet.data.datasource

import com.teameetmeet.meetmeet.data.database.dao.EventDao
import com.teameetmeet.meetmeet.data.database.entity.Event
import com.teameetmeet.meetmeet.data.toDateLong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class LocalCalendarDataSource(private val dao: EventDao) {
    suspend fun getEvents(startDate: String, endDate: String): Flow<List<Event>> {
        return startDate.toDateLong()?.let { startDateLong ->
            endDate.toDateLong()?.let { endDateLong ->
                dao.getEvents(startDateLong, endDateLong)
            }
        } ?: emptyFlow()
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

    suspend fun get(id: Int): Event {
        return dao.get(id)
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
        endDate?.toDateLong()?.let { endDateLong -> dao.updateStartDateTime(id, endDateLong) }
        color?.let { color -> dao.updateTitle(id, color) }
        notification?.let { notification -> dao.updateTitle(id, notification) }
    }
}