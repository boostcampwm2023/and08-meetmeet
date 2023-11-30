package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.databinding.ItemEventJoinMemberBinding

class EventMemberAdapter : ListAdapter<EventMemberState, EventMemberViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventMemberViewHolder {
        val binding = ItemEventJoinMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventMemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object: DiffUtil.ItemCallback<EventMemberState>() {
            override fun areItemsTheSame(
                oldItem: EventMemberState,
                newItem: EventMemberState
            ): Boolean {
                return oldItem.eventMember.id == newItem.eventMember.id
            }

            override fun areContentsTheSame(
                oldItem: EventMemberState,
                newItem: EventMemberState
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}