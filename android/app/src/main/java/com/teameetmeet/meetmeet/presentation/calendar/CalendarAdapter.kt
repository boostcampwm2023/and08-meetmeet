package com.teameetmeet.meetmeet.presentation.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemCalendarBinding
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.presentation.model.EventBar

class CalendarAdapter(
    private val calendarItemClickListener: CalendarItemClickListener
) : ListAdapter<CalendarItem, CalendarAdapter.CalendarViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        binding.root.layoutParams.height = parent.measuredHeight / 6
        return CalendarViewHolder(binding, calendarItemClickListener)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarViewHolder(
        private val binding: ItemCalendarBinding,
        private val calendarItemClickListener: CalendarItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {
        private fun onClick(item: CalendarItem) {
            calendarItemClickListener.onItemClick(item)
        }

        fun bind(item: CalendarItem) {
            with(binding) {
                this.item = item
                //todo: 이벤트 바 배치 작업..
                itemList = item.events.map {
                    EventBar(it.id, it.color, isStart = true, isEnd = true)
                }
                itemCalendarViewTouch.setOnClickListener { onClick(item) }
                itemCalendarRvEvents.itemAnimator = null
                if (itemCalendarRvEvents.adapter == null) {
                    itemCalendarRvEvents.adapter = EventBarAdapter()
                }
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