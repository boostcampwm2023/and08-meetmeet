package com.teameetmeet.meetmeet.presentation.searchevent

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivitySearchEventBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import com.teameetmeet.meetmeet.util.addUtcTimeOffset
import com.teameetmeet.meetmeet.util.removeUtcTimeOffset
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchEventBinding>(
    R.layout.activity_search_event
) {
    private val viewModel: SearchEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            vm = viewModel
            searchEventSv.setupWithSearchBar(searchEventSb)
            searchEventSpinner.setSelection(1)
            searchEventSb.setNavigationOnClickListener {
                finish()
            }
        }
        setDatePicker()
    }

    private fun setDatePicker() {
        val datePickerBuilder =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.search_event_title))

        val constraintsBuilder = CalendarConstraints.Builder()

        showDatePicker(datePickerBuilder, constraintsBuilder)
        setDatePickerBuilders(datePickerBuilder, constraintsBuilder)
    }

    private fun setDatePickerBuilders(
        datePickerBuilder: MaterialDatePicker.Builder<Pair<Long, Long>>,
        constraintsBuilder: CalendarConstraints.Builder
    ) {
        lifecycleScope.launch {
            viewModel.searchDateRange.collectLatest {
                datePickerBuilder.setSelection(
                    Pair(it.first.addUtcTimeOffset(), it.second.addUtcTimeOffset())
                )
                constraintsBuilder.setOpenAt(it.first.addUtcTimeOffset())
            }
        }
    }

    private fun showDatePicker(
        datePickerBuilder: MaterialDatePicker.Builder<Pair<Long, Long>>,
        constraintsBuilder: CalendarConstraints.Builder
    ) {
        binding.searchEventTvDateRange.setOnClickListener {
            datePickerBuilder
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
                .apply {
                    this.addOnPositiveButtonClickListener {
                        viewModel.setSearchDateRange(
                            Pair(it.first.removeUtcTimeOffset(), it.second.removeUtcTimeOffset())
                        )
                        binding.searchEventSpinner.setSelection(0)
                    }
                }.show(supportFragmentManager, "datePicker")
        }
    }
}