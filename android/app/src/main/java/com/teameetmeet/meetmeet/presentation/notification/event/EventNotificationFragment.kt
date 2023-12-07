package com.teameetmeet.meetmeet.presentation.notification.event

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.databinding.FragmentEventNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.notification.EventNotificationViewModel
import com.teameetmeet.meetmeet.presentation.notification.SwipeHelperCallback
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.toDateString
import com.teameetmeet.meetmeet.util.date.toTimeStampLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZoneId

@AndroidEntryPoint
class EventNotificationFragment :
    BaseFragment<FragmentEventNotificationBinding>(R.layout.fragment_event_notification) {

    private val viewModel: EventNotificationViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBinding()
        collectViewModelEvent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchEventNotificationList()
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is EventNotificationUiEvent.ShowAcceptDialog -> {
                            showAcceptDialog(event.event)
                        }

                        is EventNotificationUiEvent.ShowMessage -> {
                            showMessage(event.message, event.extraMessage)
                        }
                    }
                }
            }
        }
    }

    private fun setBinding() {
        binding.vm = viewModel
        binding.notificationRcv.adapter = EventNotificationAdapter(viewModel)
        val swipeHelperCallback = SwipeHelperCallback().apply {
            setClamp(resources.displayMetrics.widthPixels.toFloat() / 5)
        }
        ItemTouchHelper(swipeHelperCallback).attachToRecyclerView(binding.notificationRcv)
        binding.notificationRcv.adapter = EventNotificationAdapter(viewModel)
    }

    private fun showAcceptDialog(event: EventInvitationNotification) {
        val startDate =
            event.startDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
                .toDateString(DateTimeFormat.ISO_DATE)
        val endDate = event.endDate.toTimeStampLong(DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC"))
            .toDateString(DateTimeFormat.ISO_DATE)
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(resources.getString(R.string.notification_dialog_invite_title))
            .setIcon(R.drawable.ic_calendar)
            .setMessage(
                resources.getString(
                    R.string.notification_dialog_invite_description,
                    event.nickname.toString(),
                    event.title,
                    "$startDate ~ $endDate"
                )
            )
            .setNegativeButton(resources.getString(R.string.notification_event_invite_reject)) { _, _ ->
                viewModel.acceptEventInvite(false, event)
            }
            .setPositiveButton(resources.getString(R.string.notification_event_invite_accept)) { _, _ ->
                viewModel.acceptEventInvite(true, event)
            }
            .show()
    }
}