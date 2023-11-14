package com.teameetmeet.meetmeet.data.model

import com.teameetmeet.meetmeet.R

data class EventDate(val startDate: Long, val endDate: Long)

data class EventTime(val hour: Int, val minute: Int)

enum class EventNotification(val minutes: Long, val stringResId: Int) {
    NONE(0, R.string.notification_none),
    TEN_MINUTES(10, R.string.notification_ten_minutes_before),
    ONE_HOUR(60, R.string.notification_one_hour_before),
    ONE_DAY(24 * 60, R.string.notification_one_day_before),
    ONE_WEEK(7 * 24 * 60, R.string.notification_one_week_before)
}

enum class EventRepeatTerm(val stringResId: Int) {
    NONE(R.string.repeat_term_none),
    DAY(R.string.repeat_term_day),
    WEEK(R.string.repeat_term_week),
    MONTH(R.string.repeat_term_month),
    YEAR(R.string.repeat_term_year)
}