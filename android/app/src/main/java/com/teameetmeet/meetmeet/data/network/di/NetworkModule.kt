package com.teameetmeet.meetmeet.data.network.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.teameetmeet.meetmeet.data.network.api.AuthApi
import com.teameetmeet.meetmeet.data.network.api.CalendarApi
import com.teameetmeet.meetmeet.data.network.api.EventStoryApi
import com.teameetmeet.meetmeet.data.network.api.FakeCalendarApi
import com.teameetmeet.meetmeet.data.network.api.FakeEventStoryApi
import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.api.UserApi
import com.teameetmeet.meetmeet.data.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @Provides
    @Named("retrofitWithoutAuth")
    fun provideRetrofitWithoutAuth(
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://meetmeet.chani.pro/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    @Named("retrofitWithAuth")
    fun provideRetrofitWithAuth(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://meetmeet.chani.pro/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun provideInterceptor(
        tokenRepository: TokenRepository
    ): Interceptor {
        return object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val token = runBlocking {
                    tokenRepository.getAccessToken()
                }
                val tokenAddedRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                val response = chain.proceed(tokenAddedRequest)

                if (response.code == 418) {
                    val accessToken = runBlocking {
                        tokenRepository.refreshAccessToken()
                    }
                    val refreshedRequest = chain.request().putTokenHeader(accessToken)
                    return chain.proceed(refreshedRequest)
                }
                return response
            }

            private fun Request.putTokenHeader(accessToken: String): Request {
                return this.newBuilder()
                    .addHeader("Authorization", "Bearer $accessToken")
                    .build()
            }

        }
    }

    @Singleton
    @Provides
    fun provideCalendarApi(): CalendarApi = FakeCalendarApi()

    //AccessToken 필요한 api
    @Singleton
    @Provides
    fun provideUserApi(@Named("retrofitWithAuth") retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)


    //AccessToken 필요없는 api
    @Singleton
    @Provides
    fun provideLoginApi(@Named("retrofitWithoutAuth") retrofit: Retrofit): LoginApi =
        retrofit.create(LoginApi::class.java)


    @Singleton
    @Provides
    fun provideAuthApi(@Named("retrofitWithoutAuth") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Singleton
    @Provides
    fun provideEventStoryApi(): EventStoryApi = FakeEventStoryApi()
}