package com.teameetmeet.meetmeet.presentation.notification.follow


import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFollowNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowNotificationFragment :
    BaseFragment<FragmentFollowNotificationBinding>(R.layout.fragment_follow_notification) {

    private val viewModel: FollowNotificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFollowNotificationList()
    }

    private fun setBinding() {
        binding.vm = viewModel
        binding.notificationRcv.adapter = FollowNotificationAdapter()
    }
}