package com.teameetmeet.meetmeet.presentation.calendar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ItemCalendarBinding
import com.teameetmeet.meetmeet.presentation.model.CalendarItem

class CalendarAdapter :
    ListAdapter<CalendarItem, CalendarAdapter.CalendarViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class CalendarViewHolder(private val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CalendarItem, position: Int) {
            binding.itemCalendarTvDate.text = item.getDay()
            if ((position + 1) % 7 == 6) {
                binding.itemCalendarTvDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.blue
                    )
                )
            } else if ((position + 1) % 7 == 0) {
                binding.itemCalendarTvDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.red
                    )
                )
            } else {
                binding.itemCalendarTvDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
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