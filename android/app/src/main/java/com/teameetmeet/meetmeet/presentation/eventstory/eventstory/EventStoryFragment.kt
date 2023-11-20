package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventStoryBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventStoryFragment : BaseFragment<FragmentEventStoryBinding>(R.layout.fragment_event_story) {

    private val viewModel: EventStoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when(event) {
                        is EventStoryEvent.ShowMessage -> showMessage(event.messageId, event.extraMessage)
                        is EventStoryEvent.ShowProgressBar -> showProgressBar()
                        is EventStoryEvent.StopShowProgressBar -> stopShowProgressBar()
                    }
                }
            }
        }
    }

    private fun stopShowProgressBar() {
        binding.eventStoryIvLoading.isVisible = false
    }

    private fun showProgressBar() {
        binding.eventStoryIvLoading.isVisible = true
    }

    private fun showMessage(messageId: Int, extraMessage: String) {
        Toast.makeText(requireContext(), String.format(getString(messageId), extraMessage), Toast.LENGTH_SHORT).show()
    }

    private fun navigateToEventDetailFragment() {
        findNavController().navigate(EventStoryFragmentDirections.actionEventStoryFragmentToEventStoryDetailFragment())
    }
}