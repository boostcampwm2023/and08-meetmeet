package com.teameetmeet.meetmeet.presentation.notification.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.databinding.ItemEventNotificationBinding

class EventNotificationAdapter: ListAdapter<EventNotification, EventNotificationViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventNotificationViewHolder {
        val binding = ItemEventNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventNotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventNotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<EventNotification>() {
            override fun areItemsTheSame(
                oldItem: EventNotification,
                newItem: EventNotification
            ): Boolean {
                return oldItem.eventId == newItem.eventId
            }

            override fun areContentsTheSame(
                oldItem: EventNotification,
                newItem: EventNotification
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}