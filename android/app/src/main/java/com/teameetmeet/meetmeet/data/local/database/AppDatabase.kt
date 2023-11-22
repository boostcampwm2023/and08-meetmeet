package com.teameetmeet.meetmeet.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.teameetmeet.meetmeet.data.local.database.dao.EventDao
import com.teameetmeet.meetmeet.data.local.database.entity.Event

@Database(entities = [Event::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventEntityDao(): EventDao
}