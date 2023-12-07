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
                itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.notification_highlight
                    )
                )
            }

            else -> {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.grey3))
            }
        }

        itemView.setOnClickListener {
            eventNotificationItemClickListener.onClick(item)
        }
    }
}
