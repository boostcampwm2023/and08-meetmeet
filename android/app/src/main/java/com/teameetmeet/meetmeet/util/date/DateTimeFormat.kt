package com.teameetmeet.meetmeet.util.date

import com.teameetmeet.meetmeet.MeetMeetApp
import com.teameetmeet.meetmeet.R
import java.time.format.DateTimeFormatter

enum class DateTimeFormat(val formatter: DateTimeFormatter) {
    ISO_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_iso_time_format))),
    ISO_DATE(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_iso_date_format))),
    ISO_DATE_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_iso_date_time_format))),
    LOCAL_DATE(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_local_date_format))),
    LOCAL_DATE_YEAR_MONTH(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_local_date_year_month_format))),
    LOCAL_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_local_time_format))),
    LOCAL_DATE_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_local_date_time_format))),
    LOCAL_DATE_TIME_WO_YEAR(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_local_date_time_wo_year_format))),
    GLOBAL_DATE_TIME(DateTimeFormatter.ofPattern(MeetMeetApp.instance.getString(R.string.common_global_date_time_format)))
}
