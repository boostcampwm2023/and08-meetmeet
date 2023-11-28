package com.teameetmeet.meetmeet.presentation.searchevent

import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.repository.CalendarRepository
import com.teameetmeet.meetmeet.util.getLocalDate
import com.teameetmeet.meetmeet.util.toEndLong
import com.teameetmeet.meetmeet.util.toLocalDate
import com.teameetmeet.meetmeet.util.toStartLong
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class SearchEventViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {
    private val _searchKeyword = MutableStateFlow<String>("")
    val searchKeyword: StateFlow<String> = _searchKeyword

    private val _searchDateRange = MutableStateFlow<Pair<Long, Long>>(Pair(0, 0))
    val searchDateRange: StateFlow<Pair<Long, Long>> = _searchDateRange

    private val _searchDateRangeText = MutableStateFlow<String>("")
    val searchDateRangeText: StateFlow<String> = _searchDateRangeText

    private val _searchResultEvents = MutableStateFlow<List<EventResponse>>(emptyList())
    val searchResultEvents: StateFlow<List<EventResponse>> = _searchResultEvents

    fun setSearchKeyword(charSequence: CharSequence) {
        _searchKeyword.update {
            charSequence.toString()
        }
    }

    fun setSearchDateRange(pair: Pair<Long, Long>) {
        _searchDateRange.update { pair }
        _searchDateRangeText.update {
            "${pair.first.toLocalDate()} ~ " +
                    "${pair.second.toLocalDate()}"
        }
    }

    private fun setSearchDateRangeTwoWeeks() {
        LocalDateTime.now()
        setSearchDateRange(
            Pair(
                getLocalDate().minusWeeks(1).toStartLong(),
                getLocalDate().plusWeeks(1).toEndLong(),
            )
        )
    }

    private fun setSearchDateRangeTwoMonths() {
        setSearchDateRange(
            Pair(
                getLocalDate().minusMonths(1).toStartLong(),
                getLocalDate().plusMonths(1).toEndLong(),
            )
        )
    }

    private fun setSearchDateRangeSixMonths() {
        setSearchDateRange(
            Pair(
                getLocalDate().minusMonths(3).toStartLong(),
                getLocalDate().plusMonths(3).toEndLong(),
            )
        )
    }

    fun onRangeSelected(position: Int) {
        when (position) {
            1 -> setSearchDateRangeTwoWeeks()
            2 -> setSearchDateRangeTwoMonths()
            3 -> setSearchDateRangeSixMonths()
        }
    }
}