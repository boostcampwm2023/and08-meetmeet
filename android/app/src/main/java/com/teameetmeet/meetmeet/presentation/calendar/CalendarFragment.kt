package com.teameetmeet.meetmeet.presentation.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
    }
}