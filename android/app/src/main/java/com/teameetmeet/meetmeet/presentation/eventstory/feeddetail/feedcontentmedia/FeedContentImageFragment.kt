package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontentmedia

import android.os.Bundle
import android.view.View
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFeedContentImageBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.bindingadapter.bindThumbnailImage
import com.teameetmeet.meetmeet.presentation.bindingadapter.bindThumbnailImageCenterInside
import com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.FeedDetailFragment
import com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent.FeedContentFragment

class FeedContentImageFragment : BaseFragment<FragmentFeedContentImageBinding>(
    R.layout.fragment_feed_content_image
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            when (val parent = parentFragment) {
                is FeedDetailFragment -> {
                    itemEventFeedIv.bindThumbnailImage(
                        arguments?.getString(IMAGE_URL)
                    )
                    root.setOnClickListener {
                        parent.navigateToFeedContentDetail()
                    }
                }

                is FeedContentFragment -> {
                    itemEventFeedIv.bindThumbnailImageCenterInside(
                        arguments?.getString(IMAGE_URL)
                    )
                    root.setOnClickListener {
                        parent.changeTouchedStatus()
                    }
                }
            }
        }
    }

    companion object {
        const val IMAGE_URL = "imageUrl"

        fun create(imageUrl: String): FeedContentImageFragment {
            return FeedContentImageFragment().also {
                it.arguments = Bundle().apply {
                    putString(IMAGE_URL, imageUrl)
                }
            }
        }
    }
}