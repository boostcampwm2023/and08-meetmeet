package com.teameetmeet.meetmeet.presentation.notification

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teameetmeet.meetmeet.presentation.notification.event.EventNotificationFragment
import com.teameetmeet.meetmeet.presentation.notification.follow.FollowNotificationFragment
import com.teameetmeet.meetmeet.presentation.notification.group.GroupNotificationFragment

class NotificationAdapter(
    notificationActivity: NotificationActivity
) : FragmentStateAdapter(notificationActivity) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FollowNotificationFragment()
            }

            1 -> {
                EventNotificationFragment()
            }
            else -> {
                GroupNotificationFragment()
            }
        }
    }
}
