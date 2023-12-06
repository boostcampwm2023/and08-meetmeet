package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCalendarMonthBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm.CreateMonthCalendarFactory
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm.MonthCalendarViewModel
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm.MonthCalendarViewModelFactory
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm.OwnerMonthCalendarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MonthCalendarFragment : BaseFragment<FragmentCalendarMonthBinding>(
    R.layout.fragment_calendar_month
) {
    @Inject
    lateinit var factory: CreateMonthCalendarFactory

    private val viewModel: MonthCalendarViewModel by navGraphViewModels(R.id.nav_graph_calendar) {
        MonthCalendarViewModelFactory(
            factory,
            arguments?.getString(TYPE_KEY) ?: OwnerMonthCalendarViewModel.TYPE,
            arguments?.getInt(USER_ID) ?: -1
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println(parentFragment?.javaClass)
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

    companion object {
        const val TYPE_KEY = "type"
        const val USER_ID = "userId"
    }
}