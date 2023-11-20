package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.UserProfile

class FakeUserApi : UserApi {
    override fun getUserProfile(accessToken: String): UserProfile {
        return UserProfile(
            "https://i.ibb.co/024nfzT/rabbit.jpg",
            "코딩 천재 김근범"
        )
    }
}