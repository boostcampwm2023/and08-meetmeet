package com.teameetmeet.meetmeet.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teameetmeet.meetmeet.data.repository.UserRepository
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.util.getDayListInMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val userRepository: UserRepository
)  : ViewModel(), OnCalendarItemClickListener {

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



    private fun fetchUserProfile() {
        viewModelScope.launch {
            val accessToken = "123456778" //TODO(DATA STORE에서 ACCESS TOKEN 받아오기
            userRepository.getUserProfile(accessToken).catch {
                //TODO(DATA STORE의 유저 정보가 있으면 갱신)
            }.collect { userProfile ->
                //TODO(DATA STORE에 갱신)
                _userProfileImage.update { userProfile.profileImage.orEmpty() }
                _userNickName.update { userProfile.nickname }
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
    }
}