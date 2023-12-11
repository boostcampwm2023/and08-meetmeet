package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Feed
import com.teameetmeet.meetmeet.databinding.ItemEventFeedBinding
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnFeedItemClickListener

class EventFeedViewHolder(
    private val binding: ItemEventFeedBinding,
    private val onItemClickListener: OnFeedItemClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Feed) {
        binding.item = item
        itemView.setOnClickListener {
            onItemClickListener.onItemClick(item)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onFeedItemClickListener: OnFeedItemClickListener
        ): EventFeedViewHolder {
            return EventFeedViewHolder(
                ItemEventFeedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                onFeedItemClickListener
            )
        }
    }
}