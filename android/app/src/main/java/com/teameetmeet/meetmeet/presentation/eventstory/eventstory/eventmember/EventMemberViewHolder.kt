package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus
import com.teameetmeet.meetmeet.databinding.ItemEventJoinMemberBinding

class EventMemberViewHolder(private val binding: ItemEventJoinMemberBinding, private val eventMemberClickListener: EventMemberClickListener) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: UserWithFollowStatus) {
        binding.eventMember = item
        binding.eventJoinMemberBtnAction.setOnClickListener {
            eventMemberClickListener.onClick(item)
        }
    }
}