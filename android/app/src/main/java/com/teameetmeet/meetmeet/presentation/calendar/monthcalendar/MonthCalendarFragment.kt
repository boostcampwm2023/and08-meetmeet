package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.os.Bundle
import android.view.View
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarMonthBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MonthCalendarFragment : BaseFragment<FragmentCalendarMonthBinding>(
    R.layout.fragment_calendar_month
) {
    private val viewModel: MonthCalendarViewModel by hiltNavGraphViewModels(R.id.nav_graph_calendar)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setClickListener()
        collectViewModelEvent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchEvents()
    }

    private fun setBinding() {
        with(binding) {
            vm = viewModel
            calendarRvCalendar.adapter = MonthCalendarAdapter(viewModel)
        }
    }

    private fun setClickListener() {
        binding.fabAddEvent.setOnClickListener {
            viewModel.currentDate.value.date?.let {
                findNavController().navigate(
                    MonthCalendarFragmentDirections.actionMonthCalendarFragmentToAddEventActivity(it)
                )
            }
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.dayClickEvent.collect {
                    findNavController().navigate(
                        MonthCalendarFragmentDirections
                            .actionMonthCalendarFragmentToEventsPerDayBottomSheetFragment()
                    )
                    delay(1000)
                }
            }
        }
    }
}