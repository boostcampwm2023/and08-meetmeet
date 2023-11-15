package com.teameetmeet.meetmeet.presentation.calendar

import android.os.Bundle
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityCalendarTestBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity

class CalendarTestActivity : BaseActivity<ActivityCalendarTestBinding>(R.layout.activity_calendar_test) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val calendarFragment = CalendarFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.layout_frame_layout, calendarFragment)
            .addToBackStack(null)
            .commit()
    }
}