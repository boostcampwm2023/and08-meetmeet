package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.network.entity.AvailableResponse
import com.teameetmeet.meetmeet.data.network.entity.PasswordChangeRequest
import kotlin.random.Random

class FakeUserApi : UserApi {
    override suspend fun getUserProfile(): UserProfile {
        return UserProfile(
            "https://i.ibb.co/024nfzT/rabbit.jpg",
            "코딩 천재 김근범",
            "meetmeet@naver.com"
        )
    }

    override suspend fun deleteUser() {}

    override suspend fun checkNickNameDuplication(nickname: String): AvailableResponse {
        if (Random.nextInt() % 2 == 0) {
            throw Exception()
        }
        return AvailableResponse(true)
    }

    override suspend fun patchPassword(passwordChangeRequest: PasswordChangeRequest): UserProfile {
        TODO("Not yet implemented")
    }
}