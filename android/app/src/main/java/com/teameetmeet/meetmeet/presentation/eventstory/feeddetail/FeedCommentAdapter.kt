package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.Comment
import com.teameetmeet.meetmeet.presentation.model.EventAuthority

class FeedCommentAdapter(
    private val authority: EventAuthority,
    private val listener: CommentDeleteClickListener
) : ListAdapter<Comment, FeedCommentViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCommentViewHolder {
        return FeedCommentViewHolder.from(parent, authority, listener)
    }

    override fun onBindViewHolder(holder: FeedCommentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }
        }
    }
}