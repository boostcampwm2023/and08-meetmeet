package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFeedContentBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedContentFragment : BaseFragment<FragmentFeedContentBinding>(R.layout.fragment_feed_content) {

    private val args: FeedContentFragmentArgs by navArgs()
    private val viewModel: FeedContentViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        fetchContents()
        setTobAppBar()
        collectViewModelEvent()
    }

    private fun setTobAppBar() {
        with(binding.feedContentMtb) {
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            setOnMenuItemClickListener { menu ->
                when(menu.itemId) {
                    R.id.menu_save_image -> {
                        saveImage(binding.feedContentVpContent.currentItem)
                    }
                }
                true
            }
        }
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

    private fun saveImage(imageIndex: Int) {
        viewModel.saveImage(imageIndex)
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when(event) {
                        is FeedContentEvent.ShowMessage -> showMessage(event.messageId, event.extraMessage)
                    }
                }
            }
        }
    }

}