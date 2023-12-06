package com.teameetmeet.meetmeet.service.downloading.di

import android.content.Context
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ImageDownloadModule {

    @Singleton
    @Provides
    fun provideImageDownloadHelper(
        @ApplicationContext context: Context,
    ) = ImageDownloadHelper(context)
}