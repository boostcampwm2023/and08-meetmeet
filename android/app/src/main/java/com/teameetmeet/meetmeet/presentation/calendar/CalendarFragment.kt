package com.teameetmeet.meetmeet.presentation.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment

class CalendarFragment() : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    val viewModel: CalendarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.vm = viewModel
        super.onViewCreated(view, savedInstanceState)
    }
}