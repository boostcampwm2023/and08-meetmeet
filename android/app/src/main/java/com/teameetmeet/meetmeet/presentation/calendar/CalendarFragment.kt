package com.teameetmeet.meetmeet.presentation.calendar

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalBadgeUtils
@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setClickListener()
        collectViewModelEvent()
        setBadge()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchEvents()
    }

    private fun setBinding() {
        with(binding) {
            vm = viewModel
            calendarRvCalendar.adapter = CalendarAdapter(viewModel)
            calendarRvCalendar.itemAnimator = null
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
                    it,
                    binding.calendarIbNotification,
                    binding.calendarFlNotification
                )
            }
        }
    }

    private fun setClickListener() {
        binding.fabAddEvent.setOnClickListener {
            viewModel.currentDate.value.date?.let {
                findNavController().navigate(
                    CalendarFragmentDirections.actionCalendarFragmentToAddEventActivity(it)
                )
            }
        }

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

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dayClickEvent.collect {
                    findNavController().navigate(
                        CalendarFragmentDirections
                            .actionCalendarFragmentToBottomSheetDialog(it)
                    )
                    delay(1000)
                }
            }
        }
    }
}