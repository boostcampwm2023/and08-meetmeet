package com.teameetmeet.meetmeet.presentation.notification.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification
import com.teameetmeet.meetmeet.databinding.ItemFollowNotificationBinding

class FollowNotificationViewHolder(private val binding: ItemFollowNotificationBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: FollowNotification,
        followNotificationItemClickListener: FollowNotificationItemClickListener
    ) {
        binding.item = item
        binding.tvRemove.setOnClickListener {
            followNotificationItemClickListener.onDelete(item)
        }
    }

    fun resetSwipeState() {
        itemView.animate().cancel()
        binding.swipeView.translationX = 0f
    }

    companion object {
        fun from(parent: ViewGroup): FollowNotificationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return FollowNotificationViewHolder(
                ItemFollowNotificationBinding.inflate(
                    inflater,
                    parent,
                    false
                )
            )
        }
    }
}