package com.teameetmeet.meetmeet.presentation.notification.follow

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemFollowNotificationBinding

class FollowNotificationViewHolder(private val binding: ItemFollowNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: FollowNotification) {
        binding.item = item
    }
}