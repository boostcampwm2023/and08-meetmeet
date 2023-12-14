package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.databinding.ItemEventMemberBinding
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnItemClickListener

class EventMemberViewHolder(
    private val binding: ItemEventMemberBinding,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.root.setOnClickListener {
            onItemClickListener.onItemClick()
        }
    }

    fun bind(item: EventMember) {
        binding.item = item
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onItemClickListener: OnItemClickListener
        ): EventMemberViewHolder {
            return EventMemberViewHolder(
                ItemEventMemberBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onItemClickListener
            )
        }
    }
}