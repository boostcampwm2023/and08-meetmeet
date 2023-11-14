package com.teameetmeet.meetmeet.presentation.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ItemCalendarBinding

class CalendarAdapter() : ListAdapter<String, CalendarAdapter.CalendarViewHolder>(diffCallback) {

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
        fun bind(item: String, position: Int) {
            binding.itemCalendarTvDate.text = item
            if ((position+1) % 7 == 6) {
                binding.itemCalendarTvDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.blue))
            } else if ((position+1) % 7 == 0) {
                binding.itemCalendarTvDate.setTextColor(ContextCompat.getColor(itemView.context, R.color.red))
            }
        }
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }
}