package com.teameetmeet.meetmeet.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teameetmeet.meetmeet.data.database.dao.EventDao
import com.teameetmeet.meetmeet.data.database.entity.Event

@Database(entities = [Event::class], version = 0)
abstract class AppDataBase : RoomDatabase() {
    abstract fun eventEntityDao(): EventDao
}