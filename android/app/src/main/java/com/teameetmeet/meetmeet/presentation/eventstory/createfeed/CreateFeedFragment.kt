package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
        binding.createFeedRvMedia.adapter = MediaAdapter(viewModel)
        setTopAppBar()
        setKeyboard()
        setSelectMedia()
    }

    private fun setSelectMedia() {
        val pickMedia =
            registerForActivityResult(PickMultipleVisualMedia(10)) { uris ->
                uris?.map {
                    it.toMediaItem()
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
                        viewModel.onSave()
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

    private fun Uri.toMediaItem(): MediaItem {
        val contentResolver = requireActivity().contentResolver
        val mimeType = contentResolver.getType(this)
        val isVideo = mimeType?.startsWith("video") ?: false
        val size = contentResolver.query(this, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            val size = cursor.getLong(sizeIndex).also { cursor.close() }
            size
        } ?: 0
        return MediaItem(
            isVideo, this, size
        )
    }
}
