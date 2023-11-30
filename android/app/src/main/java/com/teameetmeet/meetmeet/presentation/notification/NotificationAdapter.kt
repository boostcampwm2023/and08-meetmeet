package com.teameetmeet.meetmeet.presentation.notification

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teameetmeet.meetmeet.presentation.follow.FollowActionType
import com.teameetmeet.meetmeet.presentation.follow.FollowSearchFragment
import com.teameetmeet.meetmeet.presentation.follow.FollowState

class NotificationAdapter(
    fragment: Fragment,
    private val actionType: FollowActionType,
    private val id: Int
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FollowSearchFragment.create(FollowState.Following, actionType, id)
            }

            else -> {
                FollowSearchFragment.create(FollowState.Follower, actionType, id)
            }
        }
    }
}
