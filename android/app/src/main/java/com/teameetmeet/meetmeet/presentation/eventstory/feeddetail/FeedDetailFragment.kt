package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.Comment
import com.teameetmeet.meetmeet.databinding.FragmentFeedDetailBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedDetailFragment :
    BaseFragment<FragmentFeedDetailBinding>(R.layout.fragment_feed_detail),
    ContentClickListener,
    CommentDeleteClickListener {
    private val viewModel: FeedDetailViewModel by viewModels()
    private val navArgs: FeedDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setFeedId(navArgs.feedId)
        viewModel.setFeedAuthority(navArgs.authority)
        setBinding()
        setCallBacks()
        setTopAppBar()
        collectViewModelEvent()
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.feedDetailEvent.collect {
                    when (it) {
                        is FeedDetailEvent.ShowMessage -> {
                            showMessage(it.messageId, it.extraMessage)
                        }

                        is FeedDetailEvent.FinishFeedDetail -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFeedDetail()
    }

    private fun setBinding() {
        with(binding) {
            vm = viewModel
            feedDetailVpMedia.adapter = FeedContentsAdapter(this@FeedDetailFragment)
            feedDetailRvComment.adapter =
                FeedCommentsAdapter(navArgs.authority, this@FeedDetailFragment)
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
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_delete_feed_detail -> {
                    showFeedDeleteConfirmationDialog()
                }
            }
            true
        }
    }

    private fun showFeedDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.feed_detail_delete_event_confirm_dialog_title))
            .setMessage(getString(R.string.feed_detail_delete_event_confirm_dialog_message))
            .setPositiveButton(R.string.story_detail_delete_event_confirm_dialog_description_delete) { _, _ ->
                viewModel.deleteFeed()
            }
            .setNegativeButton(R.string.story_detail_delete_event_dialog_description_delete_cancel) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    override fun onClick() {
        viewModel.feedDetailUiState.value.feedDetail?.contents ?: return
        findNavController().navigate(
            FeedDetailFragmentDirections.actionFeedDetailFragmentToFeedContentFragment(
                viewModel.feedDetailUiState.value.feedDetail!!.contents.toTypedArray(),
                viewModel.feedDetailUiState.value.contentPage
            )
        )
    }

    override fun onClick(comment: Comment) {
        Snackbar
            .make(binding.root,
                getString(R.string.feed_comment_delete_event_confirm_message), Snackbar.LENGTH_SHORT)
            .setAction(R.string.story_detail_delete_event_confirm_dialog_description_delete) { viewModel.deleteComment(comment) }
            .show()
    }
}