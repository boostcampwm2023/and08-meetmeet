package com.teameetmeet.meetmeet.data


class NoDataException : Exception() //데이터 요청 시 반환되는 데이터가 null일 경우
class FirstSignIn(val accessToken: String, val responseToken: String) : Exception() // 최초 로그인 일 경우