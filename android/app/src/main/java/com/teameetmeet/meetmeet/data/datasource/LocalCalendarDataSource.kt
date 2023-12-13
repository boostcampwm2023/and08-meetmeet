package com.teameetmeet.meetmeet.data.datasource

import com.teameetmeet.meetmeet.data.local.database.dao.EventDao
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalCalendarDataSource @Inject constructor(private val dao: EventDao) {

    fun getEvents(startDateTime: Long, endDateTime: Long): Flow<List<Event>> {
        return flowOf(true).map { dao.getEvents(startDateTime, endDateTime) }
    }

    suspend fun insert(event: Event) {
        dao.insert(event)
    }

    suspend fun insertEvents(events: List<Event>) {
        dao.insertEvents(*events.toTypedArray())
    }

    suspend fun update(event: Event) {
        dao.update(event)
    }

    suspend fun delete(event: Event) {
        dao.delete(event)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    suspend fun deleteEvents(startDateTime: Long, endDateTime: Long) {
        dao.deleteEvents(startDateTime, endDateTime)
    }

}