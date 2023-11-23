package com.teameetmeet.meetmeet.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.local.database.entity.Event
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.presentation.model.CalendarViewMode
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.presentation.model.toEventSimple
import com.teameetmeet.meetmeet.util.getDayListInMonth
import com.teameetmeet.meetmeet.util.toEndLong
import com.teameetmeet.meetmeet.util.toStartLong
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _dayClickEvent = MutableSharedFlow<DayClickEvent>()
    val dayClickEvent: SharedFlow<DayClickEvent> = _dayClickEvent.asSharedFlow()

    private val _events = MutableStateFlow<List<EventSimple>>(listOf())
    val events: StateFlow<List<EventSimple>> = _events

    init {
        fetchUserProfile()
        fetchEvents()
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

    private fun fetchEvents() {
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
                        allocateEventsPerDay()
                    }
            }
        }
    }

    private fun allocateEventsPerDay() {
        _daysInMonth.update {
            _daysInMonth.value.map { calendarItem ->
                calendarItem.date ?: return@map calendarItem
                calendarItem.copy(
                    events = _events.value.filter { event ->
                        val todayStart = calendarItem.date.toStartLong()
                        val todayEnd = calendarItem.date.toEndLong()
                        event.startDateTime <= todayEnd && event.endDateTime >= todayStart
                    }
                )
            }
        }
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