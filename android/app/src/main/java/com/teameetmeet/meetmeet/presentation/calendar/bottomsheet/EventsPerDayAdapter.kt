package com.teameetmeet.meetmeet.presentation.calendar.bottomsheet

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.presentation.model.EventSimple

class EventsPerDayAdapter(
    private val listener: EventItemClickListener
) : ListAdapter<EventSimple, EventsPerDayViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsPerDayViewHolder {
        return EventsPerDayViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: EventsPerDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<EventSimple>() {
            override fun areItemsTheSame(oldItem: EventSimple, newItem: EventSimple): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EventSimple, newItem: EventSimple): Boolean {
                return oldItem == newItem
            }
        }
    }
}