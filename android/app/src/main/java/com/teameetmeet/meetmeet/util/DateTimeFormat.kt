package com.teameetmeet.meetmeet.util

import com.teameetmeet.meetmeet.MeetMeetApp
import com.teameetmeet.meetmeet.R
import java.time.format.DateTimeFormatter

enum class DateTimeFormat(val formatter: DateTimeFormatter) {
    ISO_DATE(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_iso_date_format))),
    ISO_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_iso_time_format))),
    ISO_DATE_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_iso_date_time_format))),
    LOCAL_DATE(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_korea_date_format))),
    LOCAL_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_korea_time_format))),
    LOCAL_DATE_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_local_date_time_format))),
    SERVER_DATE(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.server_date_format)))
}