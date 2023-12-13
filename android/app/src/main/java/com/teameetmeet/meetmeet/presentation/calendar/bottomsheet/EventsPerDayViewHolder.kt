package com.teameetmeet.meetmeet.presentation.calendar.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemEventSimpleBinding
import com.teameetmeet.meetmeet.presentation.model.EventSimple
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.getLocalDate
import com.teameetmeet.meetmeet.util.date.toDateString
import com.teameetmeet.meetmeet.util.date.toLocalDate

class EventsPerDayViewHolder(
    private val binding: ItemEventSimpleBinding,
    private val listener: EventItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: EventSimple) {
        binding.item = item
        setDateText(item)
        itemView.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    private fun setDateText(item: EventSimple) {
        val startDate = item.startDateTime.toLocalDate()
        val endDate = item.endDateTime.toLocalDate()
        val year = getLocalDate().year

        binding.itemEventBsTvDescription.text =
            if (startDate == endDate) {
                "${
                    item.startDateTime.toDateString(DateTimeFormat.LOCAL_TIME)
                } - ${
                    item.endDateTime.toDateString(DateTimeFormat.LOCAL_TIME)
                }"
            } else {
                "${
                    item.startDateTime.toDateString(
                        if (startDate.year == year) DateTimeFormat.LOCAL_DATE_TIME_WO_YEAR
                        else DateTimeFormat.LOCAL_DATE_TIME
                    )
                } - ${
                    item.endDateTime.toDateString(
                        if (endDate.year == year) DateTimeFormat.LOCAL_DATE_TIME_WO_YEAR
                        else DateTimeFormat.LOCAL_DATE_TIME
                    )
                }"
            }
    }

    companion object {
        fun from(parent: ViewGroup, listener: EventItemClickListener): EventsPerDayViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return EventsPerDayViewHolder(
                ItemEventSimpleBinding.inflate(inflater, parent, false),
                listener
            )
        }
    }
}