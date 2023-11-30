package com.teameetmeet.meetmeet.service.notification.di

import android.content.Context
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NotificationModule {

    @Singleton
    @Provides
    fun provideNotificationHelper(@ApplicationContext context: Context) = NotificationHelper(context)
}