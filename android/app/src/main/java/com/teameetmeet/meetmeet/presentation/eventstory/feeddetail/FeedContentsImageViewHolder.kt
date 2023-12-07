package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.databinding.ItemFeedContentImageBinding

class FeedContentsImageViewHolder(
    val binding: ItemFeedContentImageBinding,
    private val contentEventListener: ContentEventListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Content) {
        binding.item = data
        itemView.setOnClickListener {
            contentEventListener.onClick()
        }
    }
}