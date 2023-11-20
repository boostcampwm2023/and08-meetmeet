package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.UserProfile
import retrofit2.http.GET

interface UserApi {

    @GET
    fun getUserProfile(accessToken: String) : UserProfile
}