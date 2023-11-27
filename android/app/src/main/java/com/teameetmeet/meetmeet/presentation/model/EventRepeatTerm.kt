package com.teameetmeet.meetmeet.presentation.model

import com.teameetmeet.meetmeet.R

enum class EventRepeatTerm(val stringResId: Int, val value: String?, val days: Int) {
    NONE(R.string.repeat_term_none, null, 0),
    DAY(R.string.repeat_term_day, "DAY", 1),
    WEEK(R.string.repeat_term_week, "WEEK", 7),
    MONTH(R.string.repeat_term_month, "MONTH", 30),
    YEAR(R.string.repeat_term_year, "YEAR", 365)
}