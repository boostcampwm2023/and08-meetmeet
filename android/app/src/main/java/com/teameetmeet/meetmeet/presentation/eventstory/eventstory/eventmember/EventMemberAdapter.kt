package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.UserStatus

class EventMemberAdapter(private val eventMemberClickListener: EventMemberClickListener) : ListAdapter<UserStatus, EventMemberViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventMemberViewHolder {
        return EventMemberViewHolder.from(parent, eventMemberClickListener)
    }

    override fun onBindViewHolder(holder: EventMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object: DiffUtil.ItemCallback<UserStatus>() {
            override fun areItemsTheSame(
                oldItem: UserStatus,
                newItem: UserStatus
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UserStatus,
                newItem: UserStatus
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}