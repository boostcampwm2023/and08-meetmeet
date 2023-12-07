package com.teameetmeet.meetmeet.presentation.notification.group

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentGroupNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.notification.GroupNotificationViewModel

class GroupNotificationFragment :
    BaseFragment<FragmentGroupNotificationBinding>(R.layout.fragment_group_notification) {

    private val viewModel: GroupNotificationViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchGroupNotificationList()
    }

    private fun setBinding() {
        binding.vm = viewModel
        binding.notificationRcv.adapter = GroupNotificationAdapter()
    }
}