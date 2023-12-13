package com.teameetmeet.meetmeet.presentation.notification.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification
import com.teameetmeet.meetmeet.databinding.ItemFollowNotificationBinding

class FollowNotificationViewHolder private constructor(
    private val binding: ItemFollowNotificationBinding,
    private val followNotificationItemClickListener: FollowNotificationItemClickListener
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: FollowNotification,
    ) {
        binding.item = item
        binding.tvRemove.setOnClickListener {
            followNotificationItemClickListener.onDeleteClick(item)
        }
        NotificationManagerCompat.from(itemView.context).cancel(item.id)
    }

    fun resetSwipeState() {
        itemView.animate().cancel()
        binding.swipeView.translationX = 0f
    }

    companion object {
        fun from(
            parent: ViewGroup,
            followNotificationItemClickListener: FollowNotificationItemClickListener
        ): FollowNotificationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return FollowNotificationViewHolder(
                ItemFollowNotificationBinding.inflate(
                    inflater,
                    parent,
                    false
                ),
                followNotificationItemClickListener
            )
        }
    }
}