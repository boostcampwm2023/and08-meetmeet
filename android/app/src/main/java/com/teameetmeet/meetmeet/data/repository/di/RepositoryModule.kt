package com.teameetmeet.meetmeet.data.repository.di

import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideLoginRepository(
        loginApi: LoginApi,
        dataStore: DataStoreHelper
    ) = LoginRepository(loginApi, dataStore)

    @Singleton
    @Provides
    fun provideUserRepository(
        userApi: UserApi,
        dataStore: DataStoreHelper
    ) = UserRepository(userApi, dataStore)

    @Singleton
    @Provides
    fun provideCalendarRepository(
        localCalendarDataSource: LocalCalendarDataSource,
        remoteCalendarDataSource: RemoteCalendarDataSource
    ) = CalendarRepository(localCalendarDataSource, remoteCalendarDataSource)

}