package com.teameetmeet.meetmeet.presentation.follow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFollowListBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FollowSearchFragment :
    BaseFragment<FragmentFollowListBinding>(R.layout.fragment_follow_list) {

    private val followViewModel: FollowViewModel by viewModels({ requireParentFragment() })
    private lateinit var followState: FollowState
    private lateinit var followAdapter: FollowAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        arguments?.let {
            followState = FollowState.fromBundle(it)
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = followViewModel
        binding.state = followState

        followAdapter = FollowAdapter()
        binding.followListRv.adapter = followAdapter
    }

    companion object {
        fun create(followState: FollowState): FollowSearchFragment {
            val fragment = FollowSearchFragment()
            fragment.arguments = followState.toBundle()
            return fragment
        }
    }
}