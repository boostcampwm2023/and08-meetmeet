package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Feed
import com.teameetmeet.meetmeet.databinding.ItemEventFeedBinding
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnFeedItemClickListener
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnItemClickListener

class EventFeedListAdapter(
    private val onItemClickListener: OnFeedItemClickListener
) : ListAdapter<Feed, EventFeedListAdapter.EventFeedViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventFeedViewHolder {
        val binding = ItemEventFeedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventFeedViewHolder(binding, onItemClickListener)
    }

    override fun onBindViewHolder(holder: EventFeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventFeedViewHolder(
        private val binding: ItemEventFeedBinding,
        onItemClickListener: OnFeedItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClickListener.onItemClick()
            }
        }
        fun bind(item: Feed) {
            binding.item = item
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Feed>() {
            override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem == newItem
            }

        }
    }
}