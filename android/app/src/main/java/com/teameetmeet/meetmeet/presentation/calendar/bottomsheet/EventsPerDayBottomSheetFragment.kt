package com.teameetmeet.meetmeet.presentation.calendar.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventsPerDayBottomSheetBinding
import com.teameetmeet.meetmeet.presentation.calendar.CalendarFragmentDirections
import com.teameetmeet.meetmeet.presentation.calendar.CalendarViewModel
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventsPerDayBottomSheetFragment : BottomSheetDialogFragment(), EventItemClickListener {
    private var _binding: FragmentEventsPerDayBottomSheetBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val viewModel: CalendarViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_events_per_day_bottom_sheet, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        lifecycleScope.launch {
            viewModel.currentDate.collectLatest {
                if (it.events.isEmpty()) dismiss()
            }
        }
    }

    private fun setBinding() {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            vm = viewModel
            eventsPerDayBsRv.adapter =
                EventsPerDayAdapter(this@EventsPerDayBottomSheetFragment)
            eventsPerDayBsBtnAddEvent.setOnClickListener {
                viewModel.currentDate.value.date?.let { date ->
                    requireParentFragment().findNavController().navigate(
                        CalendarFragmentDirections
                            .actionCalendarFragmentToAddEventActivity(date)
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(eventSimple: EventSimple) {
        requireParentFragment().findNavController().navigate(
            CalendarFragmentDirections.actionCalendarFragmentToEventStoryActivity(eventSimple.id)
        )
    }
}