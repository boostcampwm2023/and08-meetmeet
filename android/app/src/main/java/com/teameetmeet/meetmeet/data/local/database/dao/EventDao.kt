package com.teameetmeet.meetmeet.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(vararg events: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("DELETE FROM Event")
    suspend fun deleteAll()

    @Query("DELETE FROM Event WHERE endDateTime >= :startDateTime AND startDateTime <= :endDateTime")
    suspend fun deleteEvents(startDateTime: Long, endDateTime: Long)

    @Query("SELECT * FROM Event WHERE endDateTime >= :startDateTime AND startDateTime <= :endDateTime")
    suspend fun getEvents(startDateTime: Long, endDateTime: Long): List<Event>
}