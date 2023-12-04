package com.teameetmeet.meetmeet.presentation.notification.event

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemEventNotificationBinding


class EventNotificationViewHolder(private val binding: ItemEventNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: EventNotification) {
        binding.item = item
    }
}
