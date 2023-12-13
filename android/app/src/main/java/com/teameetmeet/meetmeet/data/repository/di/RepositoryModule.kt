package com.teameetmeet.meetmeet.data.repository.di

import com.teameetmeet.meetmeet.data.datasource.LocalCalendarDataSource
import com.teameetmeet.meetmeet.data.datasource.RemoteCalendarDataSource
import com.teameetmeet.meetmeet.data.local.datastore.DataStoreHelper
import com.teameetmeet.meetmeet.data.network.api.AuthApi
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.api.FollowApi
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import com.teameetmeet.meetmeet.data.repository.FollowRepository
import com.teameetmeet.meetmeet.data.repository.LoginRepository
import com.teameetmeet.meetmeet.data.repository.TokenRepository
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

    @Singleton
    @Provides
    fun provideEventStoryRepository(
        eventStoryApi: EventStoryApi,
        dataStore: DataStoreHelper
    ) = EventStoryRepository(eventStoryApi, dataStore)

    @Singleton
    @Provides
    fun provideTokenRepository(
        dataStore: DataStoreHelper,
        authApi: AuthApi
    ) = TokenRepository(dataStore, authApi)

    @Singleton
    @Provides
    fun provideFollowRepository(
        followApi: FollowApi
    ) = FollowRepository(followApi)
}