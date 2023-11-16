package com.teameetmeet.meetmeet.presentation.searchevent

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivitySearchEventBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchActivity : BaseActivity<ActivitySearchEventBinding>(
    R.layout.activity_search_event
) {
    private val viewModel: SearchEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            vm = viewModel
            lifecycleOwner = this@SearchActivity
            searchEventSv.setupWithSearchBar(searchEventSb)
            searchEventSpinner.setSelection(1)
        }

        setDatePicker()
    }

    private fun setDatePicker() {
        val datePickerBuilder =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.search_event_title))

        binding.searchEventTvDateRange.setOnClickListener {
            datePickerBuilder.build().apply {
                this.addOnPositiveButtonClickListener {
                    viewModel.setSearchDateRange(it)
                    binding.searchEventSpinner.setSelection(0)
                }
            }.show(supportFragmentManager, "datePicker")
        }

        lifecycleScope.launch {
            viewModel.searchDateRange.collectLatest {
                datePickerBuilder.setSelection(it)
            }
        }
    }
}