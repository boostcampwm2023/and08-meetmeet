package com.teameetmeet.meetmeet.presentation.notification.event

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.databinding.ItemEventNotificationBinding

class EventNotificationViewHolder(private val binding: ItemEventNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: EventInvitationNotification,
        eventNotificationItemClickListener: EventNotificationItemClickListener
    ) {
        binding.item = item
        itemView.setOnClickListener {
            eventNotificationItemClickListener.onClick(item)
        }
    }
}
