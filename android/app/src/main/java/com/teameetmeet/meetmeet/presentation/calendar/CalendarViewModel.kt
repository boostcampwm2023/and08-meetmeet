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

    private val _currentDate =
        MutableStateFlow<CalendarItem>(CalendarItem(date = LocalDate.now(), isSelected = true))
    val currentDate: StateFlow<CalendarItem> = _currentDate

    private val _daysInMonth =
        MutableStateFlow<List<CalendarItem>>(currentDate.value.date!!.getDayListInMonth(currentDate.value))
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

    private val _events = MutableStateFlow<List<EventSimple>>(listOf())
    val events: StateFlow<List<EventSimple>> = _events

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
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
            daysInMonth.collectLatest { calendarItems ->
                calendarItems.dropWhile { it.date == null }
                    .let {
                        calendarRepository.getEvents(
                            it.first().date!!.toStartLong(),
                            it.last().date!!.toEndLong()
                        )
                    }
                    .collectLatest {
                        _events.emit(it.map(Event::toEventSimple))
                        setDaysInMonth()
                    }
            }
        }
    }

    private fun setDaysInMonth() {
        _daysInMonth.update {
            allocateEventsPerDay(it)
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

    private fun allocateEventsPerDay(calendarItems: List<CalendarItem>): List<CalendarItem> {
        val temp = mutableListOf<CalendarItem>()

        for (i in calendarItems.indices) {
            temp.add(calendarItems[i])
            val today = temp[i].date
            today ?: continue

            val events = _events.value
                .filter {
                    it.startDateTime <= today.toEndLong() && it.endDateTime >= today.toStartLong()
                }.sortedBy { it.startDateTime }

            if (events.isEmpty()) continue

            val continuity = events
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
                    event.color,
                    isStart = false,
                    isEnd = event.endDateTime.toLocalDate() == today || i % 7 == 6
                )
            }

            continuity[false]?.map { event ->
                val index = eventBars.indexOf(null)
                val eventBar = EventBar(
                    id = event.id,
                    event.color,
                    isStart = true,
                    isEnd = event.endDateTime.toLocalDate() == today || i % 7 == 6
                )
                if (index != -1) eventBars[index] = eventBar else eventBars.add(eventBar)
            }

            if (eventBars.size > 5) {
                eventBars[4] = EventBar(
                    id = -1, isStart = true, isEnd = true, hiddenCount = eventBars.size - 4
                )
            }

            temp[i] = temp[i].copy(events = events, eventBars = eventBars.take(5))
        }
        return temp
    }

    fun forwardMonth() {
        _currentDate.update {
            it.copy(date = currentDate.value.date!!.minusMonths(1))
        }
        _daysInMonth.update {
            currentDate.value.date!!.getDayListInMonth(currentDate.value)
        }
    }

    fun backwardMonth() {
        _currentDate.update {
            it.copy(date = currentDate.value.date!!.plusMonths(1))
        }
        _daysInMonth.update {
            currentDate.value.date!!.getDayListInMonth(currentDate.value)
        }
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
        if (currentDate.value.date != calendarItem.date) {
            _daysInMonth.update { list ->
                list.map {
                    when (it.date) {
                        currentDate.value.date -> it.copy(isSelected = false)
                        calendarItem.date -> it.copy(isSelected = true)
                        else -> it
                    }
                }
            }
            _currentDate.update { it.copy(date = calendarItem.date) }
        }

        viewModelScope.launch {
            if (calendarItem.events.isNotEmpty()) {
                _dayClickEvent.emit(DayClickEvent(calendarItem.date, calendarItem.events))
            }
        }
    }
}