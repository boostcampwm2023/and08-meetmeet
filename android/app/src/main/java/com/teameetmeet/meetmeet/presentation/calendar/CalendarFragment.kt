package com.teameetmeet.meetmeet.presentation.calendar

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.MonthCalendarFragment
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm.OwnerMonthCalendarViewModel
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalBadgeUtils
@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setActiveNotificationCountFlow()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setClickListener()
//        setBadge()
        setNavHost()
    }

    private fun setNavHost() {
        val bundle =
            bundleOf(MonthCalendarFragment.TYPE_KEY to OwnerMonthCalendarViewModel.TYPE)
        (binding.fragmentContainer.getFragment<NavHostFragment>()).navController.setGraph(
            R.navigation.nav_graph_calendar, bundle
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserProfile()
        viewModel.fetchActiveNotificationCount()
    }

    private fun setBinding() {
        with(binding) {
            vm = viewModel
        }
    }

    private fun setBadge() {
        BadgeDrawable.create(requireContext()).apply {
            number = 5
            backgroundColor =
                ContextCompat.getColor(requireContext(), R.color.calendar_background_purple)
            badgeTextColor = ContextCompat.getColor(requireContext(), R.color.black)
            badgeGravity = BadgeDrawable.TOP_END
        }.also {
            binding.calendarFlNotification.foreground = it
            binding.calendarFlNotification.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                BadgeUtils.attachBadgeDrawable(
                    it, binding.calendarIbNotification, binding.calendarFlNotification
                )
            }
        }
    }

    private fun setClickListener() {
        binding.calendarClProfile.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToSettingActivity())
        }

        binding.calendarIbSearch.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToSearchActivity())
        }

        binding.calendarFlNotification.setOnClickListener {
            findNavController().navigate(CalendarFragmentDirections.actionCalendarFragmentToNotificationActivity())
        }
    }
}