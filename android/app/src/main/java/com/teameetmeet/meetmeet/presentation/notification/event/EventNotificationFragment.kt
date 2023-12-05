package com.teameetmeet.meetmeet.presentation.notification.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventNotificationFragment :
    BaseFragment<FragmentEventNotificationBinding>(R.layout.fragment_event_notification) {

    private val viewModel: EventNotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchEventNotificationList()
    }

    private fun setBinding() {
        binding.vm = viewModel
        binding.notificationRcv.adapter = EventNotificationAdapter()
    }
}