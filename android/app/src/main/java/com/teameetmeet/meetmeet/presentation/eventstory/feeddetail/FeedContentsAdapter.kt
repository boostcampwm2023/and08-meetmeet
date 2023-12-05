package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.databinding.ItemFeedContentBinding

class FeedContentsAdapter(
    private val contentClickListener: ContentClickListener
) : ListAdapter<Content, FeedContentsAdapter.FeedContentsViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedContentsViewHolder {
        return FeedContentsViewHolder(
            ItemFeedContentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            contentClickListener
        )
    }

    override fun onBindViewHolder(holder: FeedContentsViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    class FeedContentsViewHolder(
        val binding: ItemFeedContentBinding,
        private val contentClickListener: ContentClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Content, position: Int) {
            binding.item = data
            itemView.setOnClickListener {
                contentClickListener.onClick(position)
            }
        }
    }

    companion object {
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