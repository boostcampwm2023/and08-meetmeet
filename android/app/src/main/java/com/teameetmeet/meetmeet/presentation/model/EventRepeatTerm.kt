package com.teameetmeet.meetmeet.presentation.model

import com.teameetmeet.meetmeet.R

enum class EventRepeatTerm(val stringResId: Int, val value: String?) {
    NONE(R.string.repeat_term_none, null),
    DAY(R.string.repeat_term_day, "DAY"),
    WEEK(R.string.repeat_term_week, "WEEK"),
    MONTH(R.string.repeat_term_month, "MONTH"),
    YEAR(R.string.repeat_term_year, "YEAR")
}