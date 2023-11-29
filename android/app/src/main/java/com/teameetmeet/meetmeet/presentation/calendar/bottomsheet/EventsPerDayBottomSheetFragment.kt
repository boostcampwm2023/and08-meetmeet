package com.teameetmeet.meetmeet.presentation.calendar.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventsPerDayBottomSheetBinding
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.util.DateTimeFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventsPerDayBottomSheetFragment : BottomSheetDialogFragment(), EventItemClickListener {
    private var _binding: FragmentEventsPerDayBottomSheetBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val args: EventsPerDayBottomSheetFragmentArgs by navArgs()

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
        setRecyclerView()
    }

    private fun setBinding() {
        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            eventsPerDayTvDate.text =
                args.eventsPerDay.date.format(DateTimeFormat.ISO_DATE.formatter)
            eventsPerDayBsBtnAddEvent.setOnClickListener {
                findNavController().navigate(
                    EventsPerDayBottomSheetFragmentDirections
                        .actionBottomSheetDialogToAddEventActivity(args.eventsPerDay.date)
                )
            }
        }
    }

    private fun setRecyclerView() {
        binding.eventsPerDayBsRv.adapter = EventsPerDayAdapter(this)
        (binding.eventsPerDayBsRv.adapter as EventsPerDayAdapter)
            .submitList(args.eventsPerDay.events)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(eventSimple: EventSimple) {
        findNavController().navigate(
            EventsPerDayBottomSheetFragmentDirections.actionBottomSheetDialogToEventStoryActivity(eventSimple.id)
        )
    }
}