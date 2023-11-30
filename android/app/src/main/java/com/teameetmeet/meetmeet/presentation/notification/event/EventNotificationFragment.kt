package com.teameetmeet.meetmeet.presentation.notification.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import kotlinx.coroutines.launch

class EventNotificationFragment : BaseFragment<FragmentNotificationBinding>(R.layout.fragment_notification) {

    private val viewModel: EventNotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        collectViewModelData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchEventNotificationList()
    }

    private fun collectViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventNotificationList.collect {
                    (binding.notificationRcv.adapter as? EventNotificationAdapter)?.submitList(it)
                }
            }
        }
    }

    private fun setBinding() {
        binding.notificationRcv.adapter = EventNotificationAdapter()
    }
}