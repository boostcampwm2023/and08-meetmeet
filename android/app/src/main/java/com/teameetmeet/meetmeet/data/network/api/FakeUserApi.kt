package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.UserProfile

class FakeUserApi : UserApi {
    override fun getUserProfile(accessToken: String): UserProfile {
        return UserProfile(
            "http://image.kumoh.ac.kr/images/gbkim.jpg",
            "코딩 천재 김근범"
        )
    }
}