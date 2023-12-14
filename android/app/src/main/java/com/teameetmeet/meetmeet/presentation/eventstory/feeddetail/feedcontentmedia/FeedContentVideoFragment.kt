package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontentmedia

import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFeedContentVideoBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.FeedDetailFragment
import com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent.FeedContentFragment
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(UnstableApi::class)
class FeedContentVideoFragment : BaseFragment<FragmentFeedContentVideoBinding>(
    R.layout.fragment_feed_content_video
) {
    @Inject
    lateinit var mediaSourceFactory: DefaultMediaSourceFactory

    private val player by lazy {
        ExoPlayer
            .Builder(requireContext())
            .setMediaSourceFactory(mediaSourceFactory)
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPlayer()
        setBinding()
    }


    private fun setPlayer() {
        with(player) {
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
        }
    }

    private fun setBinding() {
        with(binding) {
            videoUrl = arguments?.getString(VIDEO_URL)
            itemEventFeedPv.player = player
            when (val parent = parentFragment) {
                is FeedDetailFragment -> {
                    itemEventFeedPv.resizeMode = RESIZE_MODE_ZOOM
                    itemEventFeedPv.useController = false
                    root.setClickEvent(viewLifecycleOwner.lifecycleScope) {
                        parent.navigateToFeedContentDetail()
                    }
                }

                is FeedContentFragment -> {
                    itemEventFeedPv.setShowNextButton(false)
                    itemEventFeedPv.setShowPreviousButton(false)
                    itemEventFeedPv.resizeMode = RESIZE_MODE_FIT
                    root.setClickEvent(viewLifecycleOwner.lifecycleScope) {
                        parent.changeTouchedStatus()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        with(player) {
            playWhenReady = true
            play()
        }
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        with(player) {
            stop()
            clearMediaItems()
            release()
        }
    }

    companion object {
        const val VIDEO_URL = "videoUrl"

        fun create(videoUrl: String): FeedContentVideoFragment {
            return FeedContentVideoFragment().also {
                it.arguments = Bundle().apply {
                    putString(VIDEO_URL, videoUrl)
                }
            }
        }
    }
}