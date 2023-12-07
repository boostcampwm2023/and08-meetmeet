package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.teameetmeet.meetmeet.presentation.model.FeedMedia
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
@OptIn(UnstableApi::class)
class MediaSourceFactoryModule {
    @Provides
    @Singleton
    fun providesSimpleCache(
        @ApplicationContext context: Context
    ): SimpleCache {
        return SimpleCache(
            File(context.cacheDir, ".exo_cache"),
            LeastRecentlyUsedCacheEvictor(FeedMedia.MEDIA_VOLUME_CONSTRAINT),
            StandaloneDatabaseProvider(context)
        )
    }

    @Provides
    @Singleton
    fun provideDataSourceFactory(
        @ApplicationContext context: Context
    ): DefaultDataSource.Factory = DefaultDataSource.Factory(context)

    @Singleton
    @Provides
    fun provideCacheDataSourceFactory(
        simpleCache: SimpleCache,
        dataSourceFactory: DefaultDataSource.Factory,
    ): CacheDataSource.Factory =
        CacheDataSource.Factory()
            .setCache(simpleCache)
            .setUpstreamDataSourceFactory(dataSourceFactory)

    @Singleton
    @Provides
    fun provideDefaultMediaSourceFactory(
        cacheDataSourceFactory: CacheDataSource.Factory,
        @ApplicationContext context: Context
    ): DefaultMediaSourceFactory =
        DefaultMediaSourceFactory(context).setDataSourceFactory(cacheDataSourceFactory)

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun provideProgressiveMediaSourceFactory(
        cacheDataSourceFactory: CacheDataSource.Factory
    ): ProgressiveMediaSource.Factory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
}