package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.UserProfile
import retrofit2.http.GET
import retrofit2.http.POST

interface UserApi {

    @GET
    fun getUserProfile(accessToken: String) : UserProfile
}