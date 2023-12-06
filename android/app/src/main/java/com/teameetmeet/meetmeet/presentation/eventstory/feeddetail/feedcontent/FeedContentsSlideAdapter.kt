package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.databinding.ItemFeedContentWithSaveBinding

class FeedContentSlideAdapter(
    private val imageClickListener: ImageClickListener
) : ListAdapter<Content, FeedContentSlideAdapter.FeedContentsViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedContentsViewHolder {
        return FeedContentsViewHolder(
            ItemFeedContentWithSaveBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            imageClickListener
        )
    }

    override fun onBindViewHolder(holder: FeedContentsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class FeedContentsViewHolder(
        val binding: ItemFeedContentWithSaveBinding,
        imageClickListener: ImageClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                imageClickListener.onClick()
            }
        }
        fun bind(data: Content) {
            binding.item = data
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