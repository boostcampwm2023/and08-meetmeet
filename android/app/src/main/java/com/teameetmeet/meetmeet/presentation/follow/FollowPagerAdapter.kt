package com.teameetmeet.meetmeet.presentation.follow

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class FollowPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FollowSearchFragment.create(FollowState.Following)
            }

            else -> {
                FollowSearchFragment.create(FollowState.Follower)
            }
        }
    }
}