package com.teameetmeet.meetmeet.presentation.searchevent

import androidx.core.util.Pair
import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.toDateString
import com.teameetmeet.meetmeet.util.toEndLong
import com.teameetmeet.meetmeet.util.toStartLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class SearchEventViewModel : ViewModel() {
    private val localDate get() = LocalDate.now().atStartOfDay().toLocalDate()

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
            "${pair.first.toDateString()} ~ ${pair.second.toDateString()}"
        }
    }

    private fun setSearchDateRangeTwoWeeks() {
        setSearchDateRange(
            Pair(
                localDate.minusWeeks(1).toStartLong(),
                localDate.plusWeeks(1).toEndLong(),
            )
        )
    }

    private fun setSearchDateRangeTwoMonths() {
        setSearchDateRange(
            Pair(
                localDate.minusMonths(1).toStartLong(),
                localDate.plusMonths(1).toEndLong(),
            )
        )
    }

    private fun setSearchDateRangeSixMonths() {
        setSearchDateRange(
            Pair(
                localDate.minusMonths(3).toStartLong(),
                localDate.plusMonths(3).toEndLong(),
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