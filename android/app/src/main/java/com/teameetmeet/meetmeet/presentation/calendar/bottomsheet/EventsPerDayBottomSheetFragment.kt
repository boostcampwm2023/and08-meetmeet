package com.teameetmeet.meetmeet.presentation.calendar.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentBottomSheetEventsPerDayBinding

class EventsPerDayBottomSheetFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentBottomSheetEventsPerDayBinding? = null
    private val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_events_per_day, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}