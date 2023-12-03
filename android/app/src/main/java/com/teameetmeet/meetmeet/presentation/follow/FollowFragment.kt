package com.teameetmeet.meetmeet.presentation.follow

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFollowBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowFragment : BaseFragment<FragmentFollowBinding>(R.layout.fragment_follow) {

    private val viewModel: FollowViewModel by viewModels()
    private lateinit var pageAdapter: FragmentStateAdapter
    private lateinit var followAdapter: FollowAdapter

    private val args: FollowFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        binding.actionType = args.actionType

        viewModel.updateFollowing(args.actionType)
        viewModel.updateFollower(args.actionType)

        setRecyclerViewAdapter()
        setPagerAdapter()
        collectViewModelEvent()

        binding.followSearchIbNavPre.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is FollowEvent.ShowMessage -> {
                            showMessage(event.message, event.extraMessage)
                        }
                    }
                }
            }
        }
    }

    private fun setRecyclerViewAdapter() {
        followAdapter = FollowAdapter(
            actionType = args.actionType,
            userClickListener = viewModel,
            id = args.id
        )
        binding.followSearchResultRv.adapter = followAdapter
    }

    private fun setPagerAdapter() {
        pageAdapter = FollowPagerAdapter(this, args.actionType, args.id)
        binding.followSearchVp.adapter = pageAdapter
        TabLayoutMediator(binding.followSearchTl, binding.followSearchVp) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.follow_following)
                }

                else -> {
                    tab.text = getString(R.string.follow_follower)
                }
            }
        }.attach()
    }
}

