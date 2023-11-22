package com.teameetmeet.meetmeet.data.network.di

import com.teameetmeet.meetmeet.data.network.api.CalendarApi
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.api.FakeCalendarApi
import com.teameetmeet.meetmeet.data.network.api.FakeEventStoryApi
import com.teameetmeet.meetmeet.data.network.api.FakeLoginApi
import com.teameetmeet.meetmeet.data.network.api.FakeUserApi
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    @Singleton
    @Provides
    @Named("serverRetrofit")
    fun providesServerRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://meetmeet.chani.pro/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideCalendarApi(): CalendarApi = FakeCalendarApi()

    @Singleton
    @Provides
    fun provideLoginApi(): LoginApi = FakeLoginApi()

    @Singleton
    @Provides
    fun provideUserApi(): UserApi = FakeUserApi()

    @Singleton
    @Provides
    fun provideEventStoryApi(): EventStoryApi = FakeEventStoryApi()
}