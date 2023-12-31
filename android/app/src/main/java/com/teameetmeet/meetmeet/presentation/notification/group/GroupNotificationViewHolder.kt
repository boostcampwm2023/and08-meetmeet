package com.teameetmeet.meetmeet.presentation.notification.group

import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.databinding.ItemGroupNotificationBinding
import com.teameetmeet.meetmeet.presentation.notification.GroupNotification


class GroupNotificationViewHolder(private val binding: ItemGroupNotificationBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GroupNotification) {
        binding.item = item
    }
}