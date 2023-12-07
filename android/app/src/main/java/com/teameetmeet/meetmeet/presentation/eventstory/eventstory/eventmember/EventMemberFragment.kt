package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventMemberBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventMemberFragment :
    BaseFragment<FragmentEventMemberBinding>(R.layout.fragment_event_member) {

    private val args: EventMemberFragmentArgs by navArgs()
    private val viewModel: EventMemberViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setTopAppBar()
        collectViewModelEvent()
    }


    override fun onResume() {
        super.onResume()
        viewModel.fetchEventMember(args.eventMember.map { it.nickname })
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

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is EventMemberEvent.ShowMessage -> {
                            showMessage(event.messageId, event.extraMessage)
                        }

                        is EventMemberEvent.NavigateToVisitCalendarActivity -> {
                            findNavController().navigate(
                                EventMemberFragmentDirections.actionEventMemberFragmentToVisitCalendarActivity(
                                    event.userId,
                                    event.userNickname
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}