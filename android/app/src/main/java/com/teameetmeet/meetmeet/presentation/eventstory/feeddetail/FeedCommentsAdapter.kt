package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Comment
import com.teameetmeet.meetmeet.databinding.ItemFeedCommentBinding

class FeedCommentsAdapter :
    ListAdapter<Comment, FeedCommentsAdapter.FeedCommentsViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCommentsViewHolder {
        return FeedCommentsViewHolder(
            ItemFeedCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FeedCommentsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FeedCommentsViewHolder(val binding: ItemFeedCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment) {
            binding.item = item
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }

        }
    }
}