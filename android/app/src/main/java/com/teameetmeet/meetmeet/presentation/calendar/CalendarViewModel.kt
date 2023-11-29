package com.teameetmeet.meetmeet.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.presentation.model.CalendarViewMode
import com.teameetmeet.meetmeet.presentation.model.EventBar
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.presentation.model.toEventSimple
import com.teameetmeet.meetmeet.util.getDayListInMonth
import com.teameetmeet.meetmeet.util.getLocalDate
import com.teameetmeet.meetmeet.util.toEndLong
import com.teameetmeet.meetmeet.util.toLocalDate
import com.teameetmeet.meetmeet.util.toStartLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val calendarRepository: CalendarRepository
) : ViewModel(), CalendarItemClickListener {

    private val _currentDate = MutableStateFlow<LocalDate>(getLocalDate())
    val currentDate: StateFlow<LocalDate> = _currentDate

    private val _daysInMonth =
        MutableStateFlow<List<CalendarItem>>(currentDate.value.getDayListInMonth(currentDate.value))
    val daysInMonth: StateFlow<List<CalendarItem>> = _daysInMonth

    private val _userProfileImage = MutableStateFlow<String>("")
    val userProfileImage: StateFlow<String> = _userProfileImage

    private val _userNickName = MutableStateFlow<String>("")
    val userNickName: StateFlow<String> = _userNickName

    private val _calendarViewMode = MutableStateFlow<CalendarViewMode>(CalendarViewMode.MONTH)
    val calendarViewMode: StateFlow<CalendarViewMode> = _calendarViewMode

    private val _dayClickEvent = MutableSharedFlow<DayClickEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val dayClickEvent: SharedFlow<DayClickEvent> = _dayClickEvent.asSharedFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().catch {
                updateUserProfile(UserProfile(null, "", ""))
            }.collect { userProfile ->
                updateUserProfile(userProfile)
            }
        }
    }

    private fun updateUserProfile(userProfile: UserProfile) {
        _userProfileImage.update { userProfile.profileImage.orEmpty() }
        _userNickName.update { userProfile.nickname }
    }

    fun fetchEvents() {
        viewModelScope.launch {
            val date = currentDate.value
            val startDateTime = date.withDayOfMonth(1).toStartLong()
            val endDateTime = date.withDayOfMonth(date.lengthOfMonth()).toEndLong()
            calendarRepository
                .getEvents(startDateTime, endDateTime)
                .collectLatest {
                    setDaysInMonth(it.map(Event::toEventSimple))
                }
        }
    }

    private fun setDaysInMonth(monthlyEvents: List<EventSimple>) {
        _daysInMonth.update {
            allocateEventsPerDay(it, monthlyEvents)
        }
    }

    val comparator: (LocalDate) -> Comparator<EventSimple> = { today ->
        Comparator { event1, event2 ->
            val endToday1 = event1.endDateTime.toLocalDate() == today
            val endToday2 = event2.endDateTime.toLocalDate() == today

            if (!endToday1 && endToday2) -1
            else if (endToday1 && !endToday2) 1
            else if (event1.startDateTime - event2.startDateTime <= 0) -1
            else 1
        }
    }

    private fun allocateEventsPerDay(
        calendarItems: List<CalendarItem>,
        monthlyEvents: List<EventSimple>
    ): List<CalendarItem> {
        val temp = mutableListOf<CalendarItem>()

        for (i in calendarItems.indices) {
            temp.add(calendarItems[i])
            val today = temp[i].date
            today ?: continue

            val todayEvents = monthlyEvents
                .filter {
                    it.startDateTime <= today.toEndLong() && it.endDateTime >= today.toStartLong()
                }.sortedBy { it.startDateTime }

            if (todayEvents.isEmpty()) continue

            val continuity = todayEvents
                .sortedWith(comparator(today))
                .groupBy { event ->
                    i % 7 != 0 && today.dayOfMonth != 1 &&
                            temp[i - 1].eventBars.any { it?.id == event.id }
                }

            var eventBars: MutableList<EventBar?> = (0..<5).map { null }.toMutableList()

            continuity[true]?.map { event ->
                val index = temp[i - 1].eventBars.indexOfFirst { it?.id == event.id }
                eventBars[index] = EventBar(
                    id = event.id,
                    color = event.color,
                    isStart = false,
                    isEnd = i % 7 == 6 || i == calendarItems.lastIndex
                            || event.endDateTime.toLocalDate() == today
                )
            }

            continuity[false]?.map { event ->
                val index = eventBars.indexOf(null)
                val eventBar = EventBar(
                    id = event.id,
                    color = event.color,
                    isStart = true,
                    isEnd = i % 7 == 6 || i == calendarItems.lastIndex
                            || event.endDateTime.toLocalDate() == today
                )
                if (index != -1) eventBars[index] = eventBar else eventBars.add(eventBar)
            }

            if (eventBars.size > 5) {
                eventBars[4] = EventBar(
                    id = -1, isStart = true, isEnd = true, hiddenCount = eventBars.size - 4
                )
            }

            temp[i] = temp[i].copy(events = todayEvents, eventBars = eventBars.take(5))
        }
        return temp
    }

    fun moveMonth(offset: Long) {
        _currentDate.update {
            it.plusMonths(offset)
        }
        _daysInMonth.update {
            currentDate.value.getDayListInMonth(currentDate.value)
        }
        fetchEvents()
    }

    fun changeViewMode(position: Int) {
        _calendarViewMode.update {
            when (position) {
                0 -> CalendarViewMode.MONTH
                1 -> CalendarViewMode.WEEK
                else -> CalendarViewMode.MONTH
            }
        }
    }

    override fun onItemClick(calendarItem: CalendarItem) {
        calendarItem.date ?: return
        if (calendarItem.events.isNotEmpty()) {
            _dayClickEvent.tryEmit(DayClickEvent(calendarItem.date, calendarItem.events))
        }
        if (currentDate.value == calendarItem.date) return
        _daysInMonth.update { list ->
            list.map {
                when (it.date) {
                    calendarItem.date -> it.copy(isSelected = true)
                    else -> it.copy(isSelected = false)
                }
            }
        }
        _currentDate.update { calendarItem.date }
    }
}