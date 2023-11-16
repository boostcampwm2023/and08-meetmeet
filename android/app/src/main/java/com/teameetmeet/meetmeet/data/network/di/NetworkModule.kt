package com.teameetmeet.meetmeet.data.network.di

import com.teameetmeet.meetmeet.data.network.api.FakeLoginApi
import com.teameetmeet.meetmeet.data.network.api.FakeUserApi
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideLoginApi(): LoginApi = FakeLoginApi()

    @Singleton
    @Provides
    fun provideUserApi(): UserApi = FakeUserApi()
}