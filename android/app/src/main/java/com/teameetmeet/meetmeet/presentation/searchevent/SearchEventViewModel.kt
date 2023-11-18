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
    private val _searchKeyword: MutableStateFlow<String> = MutableStateFlow("")
    val searchKeyword: StateFlow<String> = _searchKeyword

    private val _searchDateRange: MutableStateFlow<Pair<Long, Long>> = MutableStateFlow(Pair(0, 0))
    val searchDateRange: StateFlow<Pair<Long, Long>> = _searchDateRange

    private val _searchDateRangeText: MutableStateFlow<String> = MutableStateFlow("")
    val searchDateRangeText: StateFlow<String> = _searchDateRangeText

    val setSearchKeyword: (String) -> Unit = { keyword ->
        _searchKeyword.update { keyword }
    }

    fun setSearchDateRange(pair: Pair<Long, Long>) {
        _searchDateRange.update { pair }
        _searchDateRangeText.update {
            "${pair.first.toLocalDate(ZoneId.systemDefault())} ~ " +
                    "${pair.second.toLocalDate(ZoneId.systemDefault())}"
        }
    }

    private fun setSearchDateRangeTwoWeeks() {
        LocalDateTime.now()
        setSearchDateRange(
            Pair(
                getLocalDate().minusWeeks(1).toStartLong(ZoneId.systemDefault()),
                getLocalDate().plusWeeks(1).toEndLong(ZoneId.systemDefault()),
            )
        )
    }

    private fun setSearchDateRangeTwoMonths() {
        setSearchDateRange(
            Pair(
                getLocalDate().minusMonths(1).toStartLong(ZoneId.systemDefault()),
                getLocalDate().plusMonths(1).toEndLong(ZoneId.systemDefault()),
            )
        )
    }

    private fun setSearchDateRangeSixMonths() {
        setSearchDateRange(
            Pair(
                getLocalDate().minusMonths(3).toStartLong(ZoneId.systemDefault()),
                getLocalDate().plusMonths(3).toEndLong(ZoneId.systemDefault()),
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