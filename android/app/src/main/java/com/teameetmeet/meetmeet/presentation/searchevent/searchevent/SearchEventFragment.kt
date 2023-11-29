package com.teameetmeet.meetmeet.presentation.searchevent.searchevent

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.databinding.FragmentSearchEventBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnItemClickListener
import com.teameetmeet.meetmeet.util.addUtcTimeOffset
import com.teameetmeet.meetmeet.util.toLocalDate
import com.teameetmeet.meetmeet.util.toStartLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZoneId

@AndroidEntryPoint
class SearchEventFragment : BaseFragment<FragmentSearchEventBinding>(
    R.layout.fragment_search_event
), OnItemClickListener, SearchItemClickListener {
    private val viewModel: SearchEventViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setSearch()
        setDatePicker()
    }

    private fun setBinding() {
        with(binding) {
            vm = viewModel
            searchEventSpinner.setSelection(1)
            searchEventRv.adapter =
                SearchResultEventAdapter(this@SearchEventFragment, this@SearchEventFragment)
            searchEventIbNavPre.setOnClickListener {
                requireActivity().finish()
            }
        }
    }

    private fun setSearch() {
        with(binding) {
            searchEventEtSearch.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch()
                    true
                }
                false
            }
            searchEventIbSearch.setOnClickListener {
                performSearch()
                searchEventEtSearch.clearFocus()
            }
        }
    }

    private fun performSearch() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEventEtSearch.windowToken, 0)
        viewModel.searchEvents()
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
                        viewModel.setSearchDateRange(validateDateRange(it))
                        binding.searchEventSpinner.setSelection(0)
                    }
                }.show(requireActivity().supportFragmentManager, "datePicker")
        }
    }

    private fun validateDateRange(range: Pair<Long, Long>): Pair<Long, Long> {
        val start = range.first.toLocalDate(ZoneId.of("UTC"))
        val end = range.second.toLocalDate(ZoneId.of("UTC"))
        val std = start.plusDays(179)

        return if (end.isAfter(std)) {
            showMessage(R.string.search_event_range_constraint_message, "")
            Pair(start.toStartLong(), std.toStartLong())
        } else {
            Pair(start.toStartLong(), end.toStartLong())
        }
    }

    override fun onItemClick() {
        // todo: 프로필 이미지로..
    }

    override fun onClick(event: EventResponse) {
        findNavController().navigate(
            SearchEventFragmentDirections.actionSearchEventFragmentToEventStoryActivity(event.id)
        )
    }
}
