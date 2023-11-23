package com.teameetmeet.meetmeet.presentation.follow

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FollowPagerAdapter(
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