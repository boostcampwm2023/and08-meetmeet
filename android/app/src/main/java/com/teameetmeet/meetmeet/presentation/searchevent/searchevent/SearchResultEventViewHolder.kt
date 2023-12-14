package com.teameetmeet.meetmeet.presentation.searchevent.searchevent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.network.entity.EventResponse
import com.teameetmeet.meetmeet.databinding.ItemEventSearchBinding
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.OnItemClickListener
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventMemberListAdapter
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.toLocalDate
import com.teameetmeet.meetmeet.util.date.toTimeStampLong
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

class SearchResultEventViewHolder(
    private val binding: ItemEventSearchBinding,
    private val searchItemClickListener: SearchItemClickListener,
    private val onMemberClickListener: OnItemClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: EventResponse) {
        itemView.setOnClickListener {
            searchItemClickListener.onClick(item)
        }

        val startDate = item.startDate.toTimeStampLong(
            DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC")
        ).toLocalDate()

        val endDate = item.endDate.toTimeStampLong(
            DateTimeFormat.ISO_DATE_TIME, ZoneId.of("UTC")
        ).toLocalDate()

        setBinding(item, startDate, endDate)
    }

    private fun setBinding(
        item: EventResponse,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        with(binding) {
            this.item = item
            itemEventRvProfiles.adapter = EventMemberListAdapter(onMemberClickListener)
            itemEventTvStartYear.text = startDate.year.toString()
            itemEventTvStartDate.text = "${startDate.monthValue}.${startDate.dayOfMonth}"
            itemEventTvStartDay.text =
                startDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
            if (endDate.isAfter(startDate)) {
                itemEventTvEnd.text = "~${endDate}"
            } else {
                itemEventTvEnd.visibility = View.GONE
            }
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            searchItemClickListener: SearchItemClickListener,
            onMemberClickListener: OnItemClickListener
        ): SearchResultEventViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return SearchResultEventViewHolder(
                ItemEventSearchBinding.inflate(inflater, parent, false),
                searchItemClickListener,
                onMemberClickListener
            )
        }
    }
}