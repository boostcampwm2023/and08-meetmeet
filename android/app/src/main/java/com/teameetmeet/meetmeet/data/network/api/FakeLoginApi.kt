package com.teameetmeet.meetmeet.data.network.api

import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import kotlin.random.Random

class FakeLoginApi : LoginApi {
    override fun loginKakao(kakaoLoginRequest: KakaoLoginRequest): Int {
        val case = Random.nextInt()
        if (case % 2 == 0) {
            throw Exception("일시 오류. 다시 시도해보세요")
        }
        return 200
    }
}