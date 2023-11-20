package com.teameetmeet.meetmeet.presentation.follow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFollowBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowFragment : BaseFragment<FragmentFollowBinding>(R.layout.fragment_follow) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.followSearchVp.adapter = FollowPagerAdapter(requireActivity())

        TabLayoutMediator(binding.followSearchTl, binding.followSearchVp) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.follow_following, 24)
                }

                1 -> {
                    tab.text = getString(R.string.follow_follower, 32)
                }
            }
        }.attach()
    }

    private inner class FollowPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val fragment = FollowSearchFragment()
                    fragment.followState = FollowState.FOLLOWING
                    fragment
                }

                else -> {
                    val fragment = FollowSearchFragment()
                    fragment.followState = FollowState.FOLLOWER
                    fragment
                }
            }
        }
    }
}