package com.teameetmeet.meetmeet.presentation.follow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFollowListBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowSearchFragment :
    BaseFragment<FragmentFollowListBinding>(R.layout.fragment_follow_list) {

    private val viewModel: FollowViewModel by viewModels()
    lateinit var followState: FollowState
    private lateinit var followAdapter: FollowAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        followAdapter = FollowAdapter()
        binding.followListRv.adapter = followAdapter
    }

    override fun onResume() {
        super.onResume()
        when (followState) {
            FollowState.FOLLOWING -> viewModel.getFollowing()
            FollowState.FOLLOWER -> viewModel.getFollower()
        }
    }
}