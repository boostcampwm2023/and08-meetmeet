package com.teameetmeet.meetmeet.presentation.notification.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.databinding.ItemFollowNotificationBinding

class FollowNotificationAdapter: ListAdapter<FollowNotification, FollowNotificationViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowNotificationViewHolder {
        val binding = ItemFollowNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FollowNotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowNotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<FollowNotification>() {
            override fun areItemsTheSame(
                oldItem: FollowNotification,
                newItem: FollowNotification
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: FollowNotification,
                newItem: FollowNotification
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}