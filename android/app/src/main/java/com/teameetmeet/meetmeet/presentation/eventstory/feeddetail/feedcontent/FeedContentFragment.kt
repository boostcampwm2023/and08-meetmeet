package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFeedContentBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment

class FeedContentFragment : BaseFragment<FragmentFeedContentBinding>(R.layout.fragment_feed_content) {

    private val args: FeedContentFragmentArgs by navArgs()
    private val viewModel: FeedContentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        fetchContents()
    }

    private fun fetchContents() {
        viewModel.fetchContents(args.contents)
    }

    private fun setBinding() {
        binding.vm = viewModel
        with(binding.feedContentVpContent) {
            adapter = FeedContentSlideAdapter(viewModel)
            post { setCurrentItem(args.index, false) }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    viewModel.resetTouchStatus()
                }
            })
        }
    }

}