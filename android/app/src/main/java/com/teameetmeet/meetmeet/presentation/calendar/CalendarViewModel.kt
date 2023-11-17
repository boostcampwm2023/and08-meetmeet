package com.teameetmeet.meetmeet.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.network.entity.UserProfile
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.presentation.model.CalendarViewMode
import com.teameetmeet.meetmeet.util.getDayListInMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(), OnCalendarItemClickListener {

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

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            userRepository.getUserProfile().catch {
                fetchLocalUserProfile()
            }.collect { userProfile ->
                userRepository.fetchUserProfile(userProfile)
                updateUserProfile(userProfile)
            }
        }
    }

    private suspend fun fetchLocalUserProfile() {
        userRepository.getLocalUserProfile().catch {
            //TODO("유저 프로필을 못 불러올 때 어떻게 해야할 지 생각")
        }.collect { userProfile ->
            updateUserProfile(userProfile)
        }
    }

    private fun updateUserProfile(userProfile: UserProfile) {
        _userProfileImage.update { userProfile.profileImage.orEmpty() }
        _userNickName.update { userProfile.nickname }
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
           when(position) {
               0 -> CalendarViewMode.MONTH
               1 -> CalendarViewMode.WEEK
               else -> CalendarViewMode.MONTH
           }
       }
    }

    override fun onItemClick(calendarItem: CalendarItem) {
        calendarItem.date ?: return
        if (currentDate.value != calendarItem) {
            _daysInMonth.update { list ->
                list.map {
                    when (it) {
                        currentDate.value -> {
                            it.copy(isSelected = false)
                        }

                        calendarItem -> {
                            it.copy(isSelected = true)
                        }

                        else -> {
                            it
                        }
                    }
                }
            }
            _currentDate.update {
                it.copy(date = calendarItem.date)
            }
        }

        viewModelScope.launch {
            _dayClickEvent.emit(DayClickEvent(calendarItem.events))
        }
    }
}