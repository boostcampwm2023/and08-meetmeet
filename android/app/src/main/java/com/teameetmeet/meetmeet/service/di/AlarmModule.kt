package com.teameetmeet.meetmeet.service.di

import android.content.Context
import com.teameetmeet.meetmeet.service.AlarmHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AlarmModule {

    @Singleton
    @Provides
    fun provideAlarmHelper(@ApplicationContext context: Context) = AlarmHelper(context)
}