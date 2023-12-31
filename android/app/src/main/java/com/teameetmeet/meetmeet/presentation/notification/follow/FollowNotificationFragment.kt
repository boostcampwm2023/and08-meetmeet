package com.teameetmeet.meetmeet.presentation.notification.follow


import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentFollowNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.notification.FollowNotificationViewModel
import com.teameetmeet.meetmeet.presentation.notification.SwipeHelperCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FollowNotificationFragment :
    BaseFragment<FragmentFollowNotificationBinding>(R.layout.fragment_follow_notification) {

    private val viewModel: FollowNotificationViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        collectViewModelEvent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchFollowNotificationList()
    }

    private fun setBinding() {
        binding.vm = viewModel
        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 5)
        }
        ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(binding.notificationRcv)
        binding.notificationRcv.adapter = FollowNotificationAdapter(viewModel)
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is FollowNotificationUiEvent.ShowMessage -> {
                            showMessage(event.message, event.extraMessage)
                        }

                        is FollowNotificationUiEvent.NavigateToLoginActivity -> {
                            navigateToLoginActivity()
                        }
                    }
                }
            }
        }
    }
}