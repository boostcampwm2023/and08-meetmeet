package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCreateFeedBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.model.MediaItem
import com.teameetmeet.meetmeet.util.getMimeType
import com.teameetmeet.meetmeet.util.getSize
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateFeedFragment : BaseFragment<FragmentCreateFeedBinding>(
    R.layout.fragment_create_feed
) {
    val viewModel: CreateFeedViewModel by viewModels()
    private val navArgs: CreateFeedFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.createFeedRvMedia.adapter = MediaAdapter(viewModel)
        setTopAppBar()
        setKeyboard()
        setSelectMedia()
        collectViewModelEvent()
    }

    private fun setSelectMedia() {
        val pickMedia =
            registerForActivityResult(PickMultipleVisualMedia(10)) { uris ->
                uris?.map { uri ->
                    MediaItem(
                        uri.getMimeType()?.startsWith("video") ?: false,
                        uri,
                        uri.getSize()
                    )
                }?.let { viewModel.selectMedia(it) }
            }

        binding.createFeedCvAddPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
        }
    }

    private fun setTopAppBar() {
        with(binding.topAppBar) {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_save -> {
                        viewModel.onSave(navArgs.storyId)
                        true
                    }

                    else -> false
                }
            }
            setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun setKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.createFeedEt.requestFocus()
        binding.createFeedEt.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) imm.showSoftInput(v, 0)
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.createFeedUiEvent.collect {
                    when (it) {
                        is CreateFeedUiEvent.ShowMessage -> showMessage(
                            it.messageId,
                            it.extraMessage
                        )

                        is CreateFeedUiEvent.CreateFeedSuccess -> {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }
}
