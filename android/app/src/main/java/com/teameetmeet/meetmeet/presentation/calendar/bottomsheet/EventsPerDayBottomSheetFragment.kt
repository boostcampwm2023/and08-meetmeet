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
class EventsPerDayBottomSheetFragment : BottomSheetDialogFragment() {
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
            binding.eventsPerDayTvDate.text =
                args.eventsPerDay.date.format(DateTimeFormat.ISO_DATE.formatter)
            binding.eventsPerDayBsBtnAddEvent.setOnClickListener {
                findNavController().navigate(
                    EventsPerDayBottomSheetFragmentDirections
                        .actionBottomSheetDialogToAddEventActivity()
                )
            }
        }
    }

    private fun setRecyclerView() {
        binding.eventsPerDayBsRv.adapter = EventsPerDayAdapter(
            object : EventItemClickListener {
                override fun onClick(eventSimple: EventSimple) {
                    findNavController().navigate(
                        EventsPerDayBottomSheetFragmentDirections
                            .actionBottomSheetDialogToEventStoryActivity()
                    )
                }
            })
        (binding.eventsPerDayBsRv.adapter as EventsPerDayAdapter)
            .submitList(args.eventsPerDay.events)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}