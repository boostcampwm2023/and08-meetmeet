package com.teameetmeet.meetmeet.data

import retrofit2.HttpException


class NoDataException : Exception() //데이터 요청 시 반환되는 데이터가 null일 경우
class ExpiredTokenException(): Exception()
class ExpiredRefreshTokenException(): Exception() // refreshToken 만료인 경우

class NoIsAllTrueException(): Exception() // 일정 수정 시 isAll이 true가 아닌 경우(반복 일정 변경 시)


fun Throwable.toException(): Throwable {
    return when(this) {
        is HttpException -> {
            this.toException()
        }
        else -> this
    }
}

fun HttpException.toException(): Throwable {
    return when(this.code()) {
        STATUS_CODE_NO_AUTHORIZATION -> {
            ExpiredRefreshTokenException()
        }
        STATUS_CODE_NO_IS_ALL_TRUE -> {
            NoIsAllTrueException()
        }
        else -> {
            this
        }
    }
}

const val STATUS_CODE_OK = 200
const val STATUS_CODE_DELETE_SUCCESS = 204
const val STATUS_CODE_NO_AUTHORIZATION = 418
const val STATUS_CODE_NO_IS_ALL_TRUE = 423