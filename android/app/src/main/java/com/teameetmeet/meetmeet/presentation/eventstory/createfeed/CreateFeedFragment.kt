package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentCreateFeedBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.model.MediaItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateFeedFragment : BaseFragment<FragmentCreateFeedBinding>(
    R.layout.fragment_create_feed
) {
    val viewModel: CreateFeedViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        setKeyboard()
        setSelectMedia()
    }

    private fun setSelectMedia() {
        val pickMedia =
            registerForActivityResult(PickMultipleVisualMedia(10)) { uris ->
                uris?.map {
                    val mimeType = requireActivity().contentResolver.getType(it)
                    MediaItem(mimeType?.startsWith("video") ?: false, it)
                }?.let { viewModel.selectMedia(it) }
            }

        binding.createFeedCvAddPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageAndVideo))
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
}