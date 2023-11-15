package com.teameetmeet.meetmeet.presentation.model

import com.teameetmeet.meetmeet.R

enum class EventRepeatTerm(val stringResId: Int) {
    NONE(R.string.repeat_term_none),
    DAY(R.string.repeat_term_day),
    WEEK(R.string.repeat_term_week),
    MONTH(R.string.repeat_term_month),
    YEAR(R.string.repeat_term_year)
}