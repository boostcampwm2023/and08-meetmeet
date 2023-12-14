package com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.Feed
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnFeedItemClickListener

class EventFeedListAdapter(
    private val onItemClickListener: OnFeedItemClickListener
) : ListAdapter<Feed, EventFeedViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventFeedViewHolder {
        return EventFeedViewHolder.from(parent, onItemClickListener)
    }

    override fun onBindViewHolder(holder: EventFeedViewHolder, position: Int) {
        holder.bind(getItem(position))
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