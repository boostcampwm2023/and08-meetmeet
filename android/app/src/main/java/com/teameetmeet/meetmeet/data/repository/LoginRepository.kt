package com.teameetmeet.meetmeet.data.repository

import com.teameetmeet.meetmeet.data.network.api.LoginApi
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginRequest
import com.teameetmeet.meetmeet.data.network.entity.KakaoLoginResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val loginApi: LoginApi
) {
    fun loginKakao(id: Long): Flow<KakaoLoginResponse> {
        return flowOf(true)
            .map {
                loginApi.loginKakao(kakaoLoginRequest = KakaoLoginRequest(id.toString()))
            }.catch {
                throw it
                //추가 예외처리 필요
            }
    }
}