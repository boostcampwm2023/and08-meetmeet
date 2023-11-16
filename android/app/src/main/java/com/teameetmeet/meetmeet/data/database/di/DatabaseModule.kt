package com.teameetmeet.meetmeet.data.database.di

import android.content.Context
import androidx.room.Room
import com.teameetmeet.meetmeet.data.database.AppDatabase
import com.teameetmeet.meetmeet.data.database.dao.EventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideDao(database: AppDatabase): EventDao {
        return database.eventEntityDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room
            .databaseBuilder(context, AppDatabase::class.java, "meetmeet-local.db")
            .build()
    }
}