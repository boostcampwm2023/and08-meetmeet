package com.teameetmeet.meetmeet.presentation.calendar

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class CalendarViewModel() : ViewModel() {

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    fun forwardMonth() {
        _selectedDate.update {
            selectedDate.value.minusMonths(1)
        }
    }

    fun backwardMonth() {
        _selectedDate.update {
            selectedDate.value.plusMonths(1)
        }
    }
}