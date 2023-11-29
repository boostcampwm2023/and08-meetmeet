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
    fun deleteEvents(startDateTime: Long, endDateTime: Long)

    @Query("SELECT * FROM Event WHERE id = :id")
    fun get(id: Int): Flow<Event>

    @Query("SELECT * FROM Event WHERE endDateTime >= :startDateTime AND startDateTime <= :endDateTime")
    fun getEvents(startDateTime: Long, endDateTime: Long): Flow<List<Event>>

    @Query("UPDATE Event SET title = :title WHERE id = :id ")
    suspend fun updateTitle(id: Int, title: String)

    @Query("UPDATE Event SET startDateTime = :startDateTime WHERE id = :id ")
    suspend fun updateStartDateTime(id: Int, startDateTime: Long)

    @Query("UPDATE Event SET endDateTime = :endDateTime WHERE id = :id ")
    suspend fun updateEndDateTime(id: Int, endDateTime: Long)

    @Query("UPDATE Event SET color = :color WHERE id = :id ")
    suspend fun updateColor(id: Int, color: String)

    @Query("UPDATE Event SET notification = :notification WHERE id = :id ")
    suspend fun updateNotification(id: Int, notification: String)
}