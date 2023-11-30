package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.eventmember

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemEventJoinMemberBinding

class EventMemberViewHolder(private val binding: ItemEventJoinMemberBinding) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.eventJoinMemberBtnAction.setOnClickListener {

        }
    }

    fun bind(item: EventMemberState) {
        binding.eventMember = item
    }
}