package com.teameetmeet.meetmeet.presentation.notification.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.network.entity.FollowNotification
import com.teameetmeet.meetmeet.databinding.ItemFollowNotificationBinding
import com.teameetmeet.meetmeet.presentation.util.setClickEvent

class FollowNotificationViewHolder private constructor(
    private val binding: ItemFollowNotificationBinding,
    private val followNotificationItemClickListener: FollowNotificationItemClickListener
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.doOnAttach {
            itemView.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
                binding.item?.let { item ->
                    binding.tvRemove.setClickEvent(lifecycleOwner.lifecycleScope) {
                        followNotificationItemClickListener.onDelete(item)
                    }
                }
            }
        }
    }

    fun bind(
        item: FollowNotification,
    ) {
        binding.item = item
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