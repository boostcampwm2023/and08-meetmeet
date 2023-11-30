package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventMemberBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment

class EventMemberFragment : BaseFragment<FragmentEventMemberBinding>(R.layout.fragment_event_member) {

    private val args: EventMemberFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("test", args.eventMember.joinToString())
    }
}