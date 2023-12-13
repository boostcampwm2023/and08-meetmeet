package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.presentation.model.CalendarItem

class MonthCalendarAdapter(
    private val listener: CalendarItemClickListener
) : ListAdapter<CalendarItem, MonthCalendarViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthCalendarViewHolder {
        return MonthCalendarViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: MonthCalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
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