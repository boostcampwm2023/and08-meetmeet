package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.teameetmeet.meetmeet.service.downloading.ImageDownloadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FeedContentFragment :
    BaseFragment<FragmentFeedContentBinding>(R.layout.fragment_feed_content) {

    private val args: FeedContentFragmentArgs by navArgs()
    private val viewModel: FeedContentViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showMessage(R.string.feed_content_message_image_save_denied, "")
        }
    }

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
                when (menu.itemId) {
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            askWriteExternalStoragePermission(imageIndex)
        } else {
            viewModel.saveImage(imageIndex, ImageDownloadWorker.TYPE_DOWNLOAD_MANAGER)

        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is FeedContentEvent.ShowMessage -> showMessage(
                            event.messageId,
                            event.extraMessage
                        )
                    }
                }
            }
        }
    }

    private fun askWriteExternalStoragePermission(imageIndex: Int) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.saveImage(imageIndex, ImageDownloadWorker.TYPE_DOWNLOAD_MANAGER)
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.write_external_storage_title_request_permission)
                .setMessage(R.string.write_external_storage_message_request_permission)
                .setPositiveButton(getString(R.string.notification_positive_request_permission)) { _, _ ->
                    requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                .setNegativeButton(getString(R.string.notification_negative_request_permission)) { _, _ ->
                }
                .show()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}