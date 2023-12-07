package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.databinding.ItemFeedContentImageBinding
import com.teameetmeet.meetmeet.databinding.ItemFeedContentVideoBinding

class FeedContentsAdapter(
    private val contentEventListener: ContentEventListener
) : ListAdapter<Content, RecyclerView.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIDEO_TYPE -> {
                FeedContentsVideoViewHolder(
                    ItemFeedContentVideoBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    contentEventListener
                )
            }

            else -> {
                FeedContentsImageViewHolder(
                    ItemFeedContentImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    contentEventListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is FeedContentsImageViewHolder -> holder.bind(getItem(position))
            is FeedContentsVideoViewHolder -> holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).mimeType.startsWith("video")) VIDEO_TYPE else IMAGE_TYPE
    }

    companion object {
        private const val IMAGE_TYPE = 0
        private const val VIDEO_TYPE = 1

        private val diffCallback = object : DiffUtil.ItemCallback<Content>() {
            override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
                return oldItem == newItem
            }
        }
    }
}