package com.teameetmeet.meetmeet.presentation.notification.event

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.databinding.ItemEventNotificationBinding

class EventNotificationViewHolder(private val binding: ItemEventNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: EventInvitationNotification,
        eventNotificationItemClickListener: EventNotificationItemClickListener
    ) {
        binding.item = item
        when (item.status) {
            UserStatus.JOIN_STATUS_PENDING -> {
                binding.swipeView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.notification_highlight
                    )
                )
            }

            else -> {
                binding.swipeView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.grey3
                    )
                )
            }
        }

        binding.swipeView.setOnClickListener {
            eventNotificationItemClickListener.onClick(item)
        }
        binding.tvRemove.setOnClickListener {
            eventNotificationItemClickListener.onDelete(item)
        }
    }

    fun resetSwipeState() {
        itemView.animate().cancel()
        binding.swipeView.translationX = 0f
    }
}
