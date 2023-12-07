package com.teameetmeet.meetmeet.presentation.eventstory.createfeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.databinding.ItemCreateFeedMediaBinding
import com.teameetmeet.meetmeet.presentation.model.FeedMedia

class MediaAdapter(
    private val listener: MediaItemCancelClickListener
) : ListAdapter<FeedMedia, MediaViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = ItemCreateFeedMediaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MediaViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<FeedMedia>() {
            override fun areItemsTheSame(oldItem: FeedMedia, newItem: FeedMedia): Boolean {
                return oldItem.uri.path == newItem.uri.path
            }

            override fun areContentsTheSame(oldItem: FeedMedia, newItem: FeedMedia): Boolean {
                return oldItem == newItem
            }
        }
    }
}