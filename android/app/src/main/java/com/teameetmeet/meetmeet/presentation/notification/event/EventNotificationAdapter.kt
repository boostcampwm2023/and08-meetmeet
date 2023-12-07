package com.teameetmeet.meetmeet.presentation.notification.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.databinding.ItemEventNotificationBinding

class EventNotificationAdapter(private val eventNotificationItemClickListener: EventNotificationItemClickListener) :
    ListAdapter<EventInvitationNotification, EventNotificationViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): EventNotificationViewHolder {
        val binding = ItemEventNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return EventNotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventNotificationViewHolder, position: Int) {
        holder.bind(getItem(position), eventNotificationItemClickListener)
    }

    override fun onViewRecycled(holder: EventNotificationViewHolder) {
        super.onViewRecycled(holder)
        holder.resetSwipeState()
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<EventInvitationNotification>() {
            override fun areItemsTheSame(
                oldItem: EventInvitationNotification, newItem: EventInvitationNotification
            ): Boolean {
                return oldItem.eventId == newItem.eventId
            }

            override fun areContentsTheSame(
                oldItem: EventInvitationNotification, newItem: EventInvitationNotification
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}