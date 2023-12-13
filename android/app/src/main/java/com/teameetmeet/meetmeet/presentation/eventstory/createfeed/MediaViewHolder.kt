package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemCreateFeedMediaBinding
import com.teameetmeet.meetmeet.presentation.model.FeedMedia

class MediaViewHolder(
    private val binding: ItemCreateFeedMediaBinding,
    private val listener: MediaItemCancelClickListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FeedMedia) {
        binding.mediaItem = item
        binding.itemCreateFeedIbCancel.setOnClickListener {
            listener.onItemClick(item)
        }
    }

    companion object {
        fun from(parent: ViewGroup, listener: MediaItemCancelClickListener): MediaViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return MediaViewHolder(
                ItemCreateFeedMediaBinding.inflate(inflater, parent, false),
                listener
            )
        }
    }
}