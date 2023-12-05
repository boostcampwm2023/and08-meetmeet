package com.teameetmeet.meetmeet.presentation.notification.follow

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification

class FollowNotificationAdapter :
    ListAdapter<FollowNotification, FollowNotificationViewHolder>(diffCallback) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowNotificationViewHolder {
        return FollowNotificationViewHolder.from(parent)
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
                return oldItem.inviteId == newItem.inviteId
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