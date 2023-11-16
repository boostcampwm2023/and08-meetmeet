package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.UserProfile

interface UserApi {

    fun getUserProfile(accessToken: String) : UserProfile
}