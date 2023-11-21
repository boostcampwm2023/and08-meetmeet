package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.databinding.ItemEventMemberBinding
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnItemClickListener

class EventMemberListAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<EventMember, EventMemberListAdapter.EventMemberViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventMemberViewHolder {
        val binding =
            ItemEventMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        binding.root.setOnClickListener {
            onItemClickListener.onItemClick()
        }
        return EventMemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventMemberViewHolder(private val binding: ItemEventMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventMember) {
            binding.item = item
        }
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