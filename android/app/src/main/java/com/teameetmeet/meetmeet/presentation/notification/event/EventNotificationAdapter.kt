package com.teameetmeet.meetmeet.presentation.notification.event

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification

class EventNotificationAdapter(private val eventNotificationItemClickListener: EventNotificationItemClickListener) :
    ListAdapter<EventInvitationNotification, EventNotificationViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): EventNotificationViewHolder {
        return EventNotificationViewHolder.from(parent, eventNotificationItemClickListener)
    }

    override fun onBindViewHolder(holder: EventNotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
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