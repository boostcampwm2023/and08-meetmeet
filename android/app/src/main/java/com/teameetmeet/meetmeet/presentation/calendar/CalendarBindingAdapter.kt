package com.teameetmeet.meetmeet.presentation.calendar

import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.google.android.material.badge.BadgeDrawable
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import java.time.LocalDate

@BindingAdapter("local_date")
fun TextView.bindLocalDate(localDate: LocalDate) {
    text = localDate.format(DateTimeFormat.LOCAL_DATE_YEAR_MONTH.formatter)
}

@BindingAdapter("badge_count", "badge_drawable")
fun FrameLayout.bindBadgeCount(count: Int, drawable: BadgeDrawable) {
    drawable.number = count
    drawable.isVisible = count > 0
}