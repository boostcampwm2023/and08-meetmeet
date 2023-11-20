package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventStoryBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventFeedListAdapter
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventMemberListAdapter
import com.teameetmeet.meetmeet.util.convertDpToPx
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventStoryFragment : BaseFragment<FragmentEventStoryBinding>(R.layout.fragment_event_story) {

    private val viewModel: EventStoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setTopAppBar()
        collectViewModelEvent()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getStory(1)
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
            eventStoryRvValueEventMembers.adapter = EventMemberListAdapter(viewModel)
            eventStoryRvEventFeed.adapter = EventFeedListAdapter()
            eventStoryRvEventFeed.addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    val column = position % 3 + 1

                    if (position >= 3){
                        outRect.top = 20
                    }
                    if (column != 1){
                        outRect.left = 20
                    }
                }
            })
            vm = viewModel
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when(event) {
                        is EventStoryEvent.ShowMessage -> showMessage(event.messageId, event.extraMessage)
                    }
                }
            }
        }
    }

    private fun showMessage(messageId: Int, extraMessage: String) {
        Toast.makeText(requireContext(), String.format(getString(messageId), extraMessage), Toast.LENGTH_SHORT).show()
    }

    private fun navigateToEventDetailFragment() {
        findNavController().navigate(EventStoryFragmentDirections.actionEventStoryFragmentToEventStoryDetailFragment())
    }
}