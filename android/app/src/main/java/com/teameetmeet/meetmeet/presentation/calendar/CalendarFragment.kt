package com.teameetmeet.meetmeet.presentation.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        setClickListener()
        collectViewModelEvent()
    }

    private fun setClickListener() {
        binding.fabAddEvent.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToAddEventActivity())
        }

        binding.calendarClProfile.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToSettingActivity())
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dayClickEvent.collect {
                    findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToBottomSheet())
                }
            }
        }
    }
}