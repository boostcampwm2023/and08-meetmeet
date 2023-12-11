package com.teameetmeet.meetmeet.presentation.calendar.monthcalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Dimension
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemEventBarBinding
import com.teameetmeet.meetmeet.presentation.model.EventBar

class EventBarViewHolder(
    private val binding: ItemEventBarBinding
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: EventBar?) {
        binding.item = item
        binding.itemEventBarTvHidden.text =
            with(item) {
                if (this != null && hiddenCount > 0) "+$hiddenCount"
                else ""
            }
    }

    companion object {
        fun from(parent: ViewGroup): EventBarViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return EventBarViewHolder(
                ItemEventBarBinding.inflate(inflater, parent, false)
                    .apply {
                        root.layoutParams.height = (parent.measuredHeight / 5)
                        itemEventBarTvHidden.setTextSize(
                            Dimension.PX, parent.measuredHeight.toFloat() / 20
                        )
                    }
            )
        }
    }
}