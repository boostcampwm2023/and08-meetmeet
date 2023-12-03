package com.teameetmeet.meetmeet.presentation.notification.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.databinding.ItemGroupNotificationBinding


class GroupNotificationAdapter: ListAdapter<GroupNotification, GroupNotificationViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupNotificationViewHolder {
        val binding = ItemGroupNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GroupNotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupNotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<GroupNotification>() {
            override fun areItemsTheSame(
                oldItem: GroupNotification,
                newItem: GroupNotification
            ): Boolean {
                return oldItem.groupId == newItem.groupId
            }

            override fun areContentsTheSame(
                oldItem: GroupNotification,
                newItem: GroupNotification
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}