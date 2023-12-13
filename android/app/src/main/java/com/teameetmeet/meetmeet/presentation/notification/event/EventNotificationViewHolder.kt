package com.teameetmeet.meetmeet.presentation.notification.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.databinding.ItemEventNotificationBinding

class EventNotificationViewHolder private constructor(
    private val binding: ItemEventNotificationBinding,
    private val eventNotificationItemClickListener: EventNotificationItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: EventInvitationNotification,
    ) {
        binding.item = item
        when (item.status) {
            UserStatus.JOIN_STATUS_PENDING -> {
                binding.swipeView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.notification_highlight
                    )
                )
            }

            else -> {
                binding.swipeView.setBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context, R.color.grey3
                    )
                )
            }
        }

        binding.swipeView.setOnClickListener {
            eventNotificationItemClickListener.onInviteClick(item)
        }
        binding.tvRemove.setOnClickListener {
            eventNotificationItemClickListener.onDeleteClick(item)
        }
    }

    fun resetSwipeState() {
        itemView.animate().cancel()
        binding.swipeView.translationX = 0f
    }

    companion object {
        fun from(
            parent: ViewGroup,
            eventNotificationItemClickListener: EventNotificationItemClickListener
        ): EventNotificationViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return EventNotificationViewHolder(
                ItemEventNotificationBinding.inflate(
                    inflater, parent, false
                ), eventNotificationItemClickListener
            )
        }
    }
}
