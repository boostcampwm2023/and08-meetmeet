package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.databinding.ItemEventJoinMemberBinding

class EventMemberViewHolder(
    private val binding: ItemEventJoinMemberBinding,
    private val eventMemberClickListener: EventMemberClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UserStatus) {
        binding.eventMember = item
        binding.eventJoinMemberBtnAction.setOnClickListener {
            eventMemberClickListener.onButtonClick(item)
        }
        itemView.setOnClickListener {
            eventMemberClickListener.onItemViewClick(item)
        }
    }

    companion object {
        fun from(parent: ViewGroup, eventMemberClickListener: EventMemberClickListener) : EventMemberViewHolder {
            return EventMemberViewHolder(
                ItemEventJoinMemberBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                eventMemberClickListener
            )
        }
    }
}