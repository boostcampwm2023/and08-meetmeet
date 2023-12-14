package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnItemClickListener

class EventMemberListAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<EventMember, EventMemberViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventMemberViewHolder {
        return EventMemberViewHolder.from(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: EventMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<EventMember>() {
            override fun areItemsTheSame(oldItem: EventMember, newItem: EventMember): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EventMember, newItem: EventMember): Boolean {
                return oldItem == newItem
            }

        }
    }
}