package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemCreateFeedMediaBinding
import com.teameetmeet.meetmeet.presentation.model.MediaItem

class MediaViewHolder(
    private val binding: ItemCreateFeedMediaBinding,
    private val listener: MediaItemCancelClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MediaItem) {
        binding.mediaItem = item
        binding.itemCreateFeedIbCancel.setOnClickListener {
            listener.onItemClick(item)
        }
    }
}