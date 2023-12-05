package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFeedDetailBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FeedDetailFragment : BaseFragment<FragmentFeedDetailBinding>(R.layout.fragment_feed_detail), ContentClickListener {
    private val viewModel: FeedDetailViewModel by viewModels()
    private val navArgs: FeedDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setFeedId(navArgs.feedId)
        setBinding()
        setCallBacks()
        setTopAppBar()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFeedDetail()
    }

    private fun setBinding() {
        with(binding) {
            vm = viewModel
            feedDetailVpMedia.adapter = FeedContentsAdapter(this@FeedDetailFragment)
            feedDetailRvComment.adapter = FeedCommentsAdapter()
        }
    }

    private fun setCallBacks() {
        with(binding) {
            feedDetailVpMedia.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.setContentPage(position)
                }
            })
            feedDetailIbCommentSend.setOnClickListener {
                viewModel.addComment()
                val imm =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.feedDetailEtComment.windowToken, 0)
                feedDetailNsv.fullScroll(View.FOCUS_DOWN)
                feedDetailRvComment.scrollToPosition(
                    feedDetailRvComment.adapter?.itemCount?.minus(1) ?: 0
                )
            }
        }
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onClick(index: Int) {
        viewModel.feedDetailUiState.value.feedDetail?.contents?: return
        findNavController().navigate(FeedDetailFragmentDirections.actionFeedDetailFragmentToFeedContentFragment(viewModel.feedDetailUiState.value.feedDetail!!.contents.toTypedArray(), index))
    }
}