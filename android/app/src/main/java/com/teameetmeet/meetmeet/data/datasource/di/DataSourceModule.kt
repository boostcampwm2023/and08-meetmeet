package com.teameetmeet.meetmeet.data.datasource.di

import com.teameetmeet.meetmeet.data.database.dao.EventDao
import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.network.api.CalendarApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    @Singleton
    @Provides
    fun provideLocalCalendarDataSource(eventDao: EventDao) = LocalCalendarDataSource(eventDao)

    @Singleton
    @Provides
    fun provideRemoteCalendarDataSource(calendarApi: CalendarApi) =
        RemoteCalendarDataSource(calendarApi)
}