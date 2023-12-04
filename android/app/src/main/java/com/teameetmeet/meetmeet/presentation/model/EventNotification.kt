package com.teameetmeet.meetmeet.presentation.model

import com.teameetmeet.meetmeet.R

enum class EventNotification(val minutes: Int, val stringResId: Int) {
    NONE(-1, R.string.notification_none),
    ON_TIME(0, R.string.notification_on_time),
    TEN_MINUTES(10, R.string.notification_ten_minutes_before),
    ONE_HOUR(60, R.string.notification_one_hour_before),
    ONE_DAY(24 * 60, R.string.notification_one_day_before),
    ONE_WEEK(7 * 24 * 60, R.string.notification_one_week_before)
}