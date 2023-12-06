package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreateMonthCalendarFactory @Inject constructor(
    private val calendarRepository: CalendarRepository,
) {
    fun createOwnerMonthCalendarViewModel(): MonthCalendarViewModel =
        OwnerMonthCalendarViewModel(calendarRepository)

    fun createOthersMonthCalendarViewModel(userId: Int): MonthCalendarViewModel =
        OthersMonthCalendarViewModel(calendarRepository, userId)
}

class MonthCalendarViewModelFactory(
    private val factory: CreateMonthCalendarFactory,
    private val type: String,
    private val userId: Int = -1
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (type) {
            OwnerMonthCalendarViewModel.TYPE -> factory.createOwnerMonthCalendarViewModel()
            OthersMonthCalendarViewModel.TYPE -> factory.createOthersMonthCalendarViewModel(userId)
            else -> factory.createOwnerMonthCalendarViewModel()
        } as T
    }
}