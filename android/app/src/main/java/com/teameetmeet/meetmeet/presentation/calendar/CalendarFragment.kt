package com.teameetmeet.meetmeet.presentation.calendar

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalBadgeUtils @AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        setClickListener()
        setBadge()
    }

    private fun setBadge() {
        BadgeDrawable.create(requireContext()).apply {
            number = 5
            backgroundColor = ContextCompat.getColor(requireContext(), R.color.calendar_background_purple)
            badgeTextColor = ContextCompat.getColor(requireContext(), R.color.black)
            badgeGravity = BadgeDrawable.TOP_END
        }.also {
            binding.calendarFlNotification.foreground = it
            binding.calendarFlNotification.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                BadgeUtils.attachBadgeDrawable(it, binding.calendarIbNotification, binding.calendarFlNotification)
            }
        }
    }

    private fun setClickListener() {
        binding.fabAddEvent.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToAddEventActivity())
        }

        binding.calendarClProfile.setOnClickListener {

        }

        binding.calendarIbSearch.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToSearchActivity())
        }

        binding.calendarFlNotification.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToNotificationActivity())
        }
    }
}