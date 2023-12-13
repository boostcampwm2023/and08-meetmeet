package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemCalendarBinding
import com.teameetmeet.meetmeet.presentation.model.CalendarItem

class MonthCalendarViewHolder(
    private val binding: ItemCalendarBinding,
    private val listener: CalendarItemClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CalendarItem) {
        with(binding) {
            this.item = item
            itemCalendarViewTouch.setOnClickListener {
                listener.onItemClick(item)
            }
            if (itemCalendarRvEvents.adapter == null) {
                itemCalendarRvEvents.adapter = EventBarAdapter()
            }
            itemCalendarRvEvents.itemAnimator = null
        }
    }

    companion object {
        fun from(parent: ViewGroup, listener: CalendarItemClickListener): MonthCalendarViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return MonthCalendarViewHolder(
                ItemCalendarBinding.inflate(inflater, parent, false)
                    .apply { root.layoutParams.height = parent.measuredHeight / 6 },
                listener
            )
        }
    }
}