package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus
import com.teameetmeet.meetmeet.databinding.ItemEventJoinMemberBinding

class EventMemberAdapter(private val eventMemberClickListener: EventMemberClickListener) : ListAdapter<UserWithFollowStatus, EventMemberViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventMemberViewHolder {
        val binding = ItemEventJoinMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventMemberViewHolder(binding, eventMemberClickListener)
    }

    override fun onBindViewHolder(holder: EventMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object: DiffUtil.ItemCallback<UserWithFollowStatus>() {
            override fun areItemsTheSame(
                oldItem: UserWithFollowStatus,
                newItem: UserWithFollowStatus
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UserWithFollowStatus,
                newItem: UserWithFollowStatus
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}