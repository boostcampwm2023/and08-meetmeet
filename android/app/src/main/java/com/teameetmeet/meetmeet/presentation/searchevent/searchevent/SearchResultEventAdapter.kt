package com.teameetmeet.meetmeet.presentation.searchevent.searchevent

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnItemClickListener

class SearchResultEventAdapter(
    private val searchItemClickListener: SearchItemClickListener,
    private val onMemberClickListener: OnItemClickListener
) : ListAdapter<EventResponse, SearchResultEventViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultEventViewHolder {
        return SearchResultEventViewHolder.from(
            parent,
            searchItemClickListener,
            onMemberClickListener
        )
    }

    override fun onBindViewHolder(holder: SearchResultEventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<EventResponse>() {
            override fun areItemsTheSame(oldItem: EventResponse, newItem: EventResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: EventResponse,
                newItem: EventResponse
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}