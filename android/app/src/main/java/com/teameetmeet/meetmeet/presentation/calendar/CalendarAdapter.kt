package com.teameetmeet.meetmeet.presentation.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemCalendarBinding
import com.teameetmeet.meetmeet.presentation.model.CalendarItem

class CalendarAdapter(
    private val onCalendarItemClickListener: OnCalendarItemClickListener
) :
    ListAdapter<CalendarItem, CalendarAdapter.CalendarViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(binding, onCalendarItemClickListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarViewHolder(
        private val binding: ItemCalendarBinding,
        private val onCalendarItemClickListener: OnCalendarItemClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        private fun onClick(item: CalendarItem) {
            onCalendarItemClickListener.onItemClick(item)
        }

        fun bind(item: CalendarItem, position: Int) {
            binding.item = item
            itemView.setOnClickListener {
                onClick(item)
            }
            if (item.isSelected) {
                itemView.setBackgroundResource(R.color.calendar_background_purple)
            } else {
                itemView.background = null
            }
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<CalendarItem>() {
            override fun areItemsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
                return oldItem.date == newItem.date
            }

            override fun areContentsTheSame(oldItem: CalendarItem, newItem: CalendarItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}