package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventStoryBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventFeedListAdapter
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventMemberListAdapter
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventStoryFragment : BaseFragment<FragmentEventStoryBinding>(R.layout.fragment_event_story), OnFeedItemClickListener {

    private val viewModel: EventStoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setTopAppBar()
        setClickListener()
        collectViewModelEvent()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getStory(1)
    }

    private fun setClickListener() {
        with(binding) {
            eventStoryIvChangeNotification.setOnClickListener {
                val dialog =
                    NotificationChangeDialog(requireContext(), viewModel, viewModel.getNoti())
                dialog.show()
            }
            eventStoryTvValueEventNotification.setOnClickListener {
                showDialog(viewModel.getNoti())
            }
            eventStoryIbSeeMoreMember.setOnClickListener {
                //TODO("더보기")
            }
            eventStoryCvInviteMember.setOnClickListener {
                when(viewModel.eventStoryUiState.value.authority) {
                    EventAuthority.GUEST -> {}//TODO("참여 신청")
                    EventAuthority.OWNER -> {}//TODO("초대 페이지로 이동")
                    else -> return@setOnClickListener
                }
            }
        }
    }

    private fun setTopAppBar() {
        binding.eventStoryTbl.setNavigationOnClickListener {
            requireActivity().finish()
        }
        binding.eventStoryTbl.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_see_more_event_story -> {
                    navigateToEventDetailFragment()
                }
            }
            true
        }
    }

    private fun setBinding() {
        with(binding) {
            eventStoryRvValueEventMembers.itemAnimator = null
            eventStoryRvValueEventMembers.adapter = EventMemberListAdapter(viewModel)
            eventStoryRvEventFeed.adapter = EventFeedListAdapter(this@EventStoryFragment)
            vm = viewModel
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is EventStoryEvent.ShowMessage -> showMessage(
                            event.messageId,
                            event.extraMessage
                        )
                    }
                }
            }
        }
    }


    private fun navigateToEventDetailFragment() {
        findNavController().navigate(EventStoryFragmentDirections.actionEventStoryFragmentToEventStoryDetailFragment(viewModel.eventStoryUiState.value.eventStory?.id ?:0))
    }

    private fun showDialog(noti: String) {
        AlertDialog.Builder(requireContext()).apply {
            setMessage(noti).create().show()
        }
    }

    override fun onItemClick() {
        findNavController().navigate(EventStoryFragmentDirections.actionEventStoryFragmentToFeedDetailFragment())
    }
}