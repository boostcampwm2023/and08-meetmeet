package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventMemberBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventMemberFragment : BaseFragment<FragmentEventMemberBinding>(R.layout.fragment_event_member) {

    private val args: EventMemberFragmentArgs by navArgs()
    private val viewModel: EventMemberViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setTopAppBar()
    }


    override fun onResume() {
        super.onResume()
        viewModel.fetchEventMember(args.eventMember.map{it.nickname})
    }

    private fun setBinding() {
        binding.eventMemberRcv.adapter = EventMemberAdapter(viewModel)
        binding.vm = viewModel
    }

    private fun setTopAppBar() {
        binding.eventMemberMtb.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}