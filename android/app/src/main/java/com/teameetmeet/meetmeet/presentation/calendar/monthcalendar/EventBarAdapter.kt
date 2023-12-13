package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.presentation.model.EventBar

class EventBarAdapter : ListAdapter<EventBar?, EventBarViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventBarViewHolder {
        return EventBarViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EventBarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<EventBar?>() {
            override fun areItemsTheSame(oldItem: EventBar, newItem: EventBar): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: EventBar, newItem: EventBar): Boolean {
                return oldItem == newItem
            }
        }
    }
}