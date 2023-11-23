package com.teameetmeet.meetmeet.presentation.calendar.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemEventSimpleBinding
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.util.DateTimeFormat
import com.teameetmeet.meetmeet.util.toDateString

class EventsPerDayAdapter(
    private val listener: EventItemClickListener
) : ListAdapter<EventSimple, EventsPerDayAdapter.EventsPerDayViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsPerDayViewHolder {
        val binding = ItemEventSimpleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventsPerDayViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: EventsPerDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EventsPerDayViewHolder(
        private val binding: ItemEventSimpleBinding,
        private val listener: EventItemClickListener,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EventSimple) {
            binding.item = item
            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
            binding.itemEventBsTvDescription.text =
                "${item.startDateTime.toDateString(DateTimeFormat.LOCAL_DATE_TIME)} ~ ${
                    item.endDateTime.toDateString(DateTimeFormat.LOCAL_DATE_TIME)
                }"
        }
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