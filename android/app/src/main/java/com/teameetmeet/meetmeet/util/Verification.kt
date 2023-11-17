package com.teameetmeet.meetmeet.util

object Verification {

    private val emailRegex = """^[a-zA-Z0-9+-\_.]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$""".toRegex()
    private val passwordRegex = """^(?=.*[a-zA-Z])(?=.*[!@#*])(?=.*[0-9]).{9,14}$""".toRegex()

    fun isValidEmail(email: String): Boolean = email.matches(emailRegex)
    fun isValidPassword(password: String): Boolean = password.matches(passwordRegex)
}