package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFeedDetailBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailFragment : BaseFragment<FragmentFeedDetailBinding>(R.layout.fragment_feed_detail) {
    private val viewModel: FeedDetailViewModel by viewModels()
    private val navArgs: FeedDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setFeedId(navArgs.feedId)
        setBinding()
        setTopAppBar()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFeedDetail()
    }

    private fun setBinding() {
        with(binding) {
            vm = viewModel
            feedDetailVpMedia.adapter = FeedContentsAdapter()
            feedDetailRvComment.adapter = FeedCommentsAdapter()
        }
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}