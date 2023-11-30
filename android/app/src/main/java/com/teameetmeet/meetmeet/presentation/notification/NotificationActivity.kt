package com.teameetmeet.meetmeet.presentation.notification

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity

class NotificationActivity : BaseActivity<ActivityNotificationBinding>(R.layout.activity_notification) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTabLayout()
    }

    private fun setTabLayout() {
        binding.notificationVp.adapter = NotificationAdapter(this)

        TabLayoutMediator(binding.notificationTl, binding.notificationVp) {tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.notification_tab_follow)
                1 -> tab.text = getString(R.string.notification_tab_invite_event)
                2 -> tab.text = getString(R.string.notification_tab_invite_group)
            }
        }.attach()
    }
}