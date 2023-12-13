package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.Feed
import com.teameetmeet.meetmeet.databinding.FragmentEventStoryBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.eventstory.EventStoryActivity
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventFeedListAdapter
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventMemberListAdapter
import com.teameetmeet.meetmeet.presentation.model.EventAuthority
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventStoryFragment : BaseFragment<FragmentEventStoryBinding>(R.layout.fragment_event_story),
    OnFeedItemClickListener {

    private val viewModel: EventStoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEventId()
        setBinding()
        setTopAppBar()
        setClickListener()
        collectViewModelEvent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getStory()
    }

    private fun setEventId() {
        val eventId = arguments?.getInt(EventStoryActivity.EVENT_ID)
        viewModel.setEventId(eventId)
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
                findNavController().navigate(EventStoryFragmentDirections.actionEventStoryFragmentToEventMemberFragment(viewModel.eventStoryUiState.value.eventStory?.eventMembers?.toTypedArray().orEmpty()))
            }
            eventStoryCvInviteMember.setOnClickListener {
                when (viewModel.eventStoryUiState.value.authority) {
                    EventAuthority.GUEST -> {
                        viewModel.joinEventStory()
                    }
                    EventAuthority.OWNER -> {
                        viewModel.eventStoryUiState.value.eventStory?.let { story ->
                            findNavController().navigate(
                                EventStoryFragmentDirections.actionEventStoryFragmentToFollowFragment()
                                    .setId(story.id)
                            )
                        }
                    }
                    else -> return@setOnClickListener
                }
            }
            eventStoryFabMakeFeed.setOnClickListener {
                findNavController().navigate(
                    EventStoryFragmentDirections.actionEventStoryFragmentToCreateFeedFragment(
                        viewModel.eventStoryUiState.value.eventId
                    )
                )
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
                        is EventStoryUiEvent.ShowMessage -> showMessage(
                            event.messageId, event.extraMessage
                        )

                        is EventStoryUiEvent.NavigateToLoginActivity -> {
                            navigateToLoginActivity()
                        }
                    }
                }
            }
        }
    }

    private fun navigateToEventDetailFragment() {
        findNavController().navigate(
            EventStoryFragmentDirections.actionEventStoryFragmentToEventStoryDetailFragment(
                viewModel.eventStoryUiState.value.eventId
            )
        )
    }

    private fun showDialog(noti: String) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.event_story_title_event_notification)
                .setMessage(noti)
                .setNeutralButton(R.string.event_story_delete_event_notification) { _, _ ->
                    viewModel.editAnnouncement(null)
                }
                .create().show()
        }
    }

    override fun onItemClick(feed: Feed) {
        findNavController().navigate(
            EventStoryFragmentDirections.actionEventStoryFragmentToFeedDetailFragment(
                feed.id,
                viewModel.eventStoryUiState.value.authority
            )
        )
    }
}