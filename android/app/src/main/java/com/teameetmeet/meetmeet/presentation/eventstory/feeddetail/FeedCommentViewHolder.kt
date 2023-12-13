package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Comment
import com.teameetmeet.meetmeet.databinding.ItemFeedCommentBinding
import com.teameetmeet.meetmeet.presentation.model.EventAuthority

class FeedCommentViewHolder(
    private val binding: ItemFeedCommentBinding,
    private val authority: EventAuthority,
    private val listener: CommentDeleteClickListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Comment) {
        binding.item = item
        binding.feedCommentBtnDelete.isVisible =
            item.isMine || authority == EventAuthority.OWNER
        binding.feedCommentBtnDelete.setOnClickListener {
            listener.onClick(item)
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            authority: EventAuthority,
            listener: CommentDeleteClickListener
        ): FeedCommentViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return FeedCommentViewHolder(
                ItemFeedCommentBinding.inflate(inflater, parent, false),
                authority,
                listener
            )
        }
    }
}