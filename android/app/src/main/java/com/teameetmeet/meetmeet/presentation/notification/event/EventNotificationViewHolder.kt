package com.teameetmeet.meetmeet.presentation.notification.event

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.data.network.entity.EventInvitationNotification
import com.teameetmeet.meetmeet.databinding.ItemEventNotificationBinding
import com.teameetmeet.meetmeet.presentation.util.setClickEvent

class EventNotificationViewHolder private constructor(
    private val binding: ItemEventNotificationBinding,
    private val eventNotificationItemClickListener: EventNotificationItemClickListener
) : RecyclerView.ViewHolder(binding.root) {

    init {
        itemView.doOnAttach {
            itemView.findViewTreeLifecycleOwner()?.let { lifecycleOwner ->
                binding.item?.let { item ->
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

                    binding.swipeView.setClickEvent(lifecycleOwner.lifecycleScope) {
                        eventNotificationItemClickListener.onClick(item)
                    }
                    binding.tvRemove.setClickEvent(lifecycleOwner.lifecycleScope) {
                        eventNotificationItemClickListener.onDelete(item)
                    }
                }
            }
        }
    }

    fun bind(
        item: EventInvitationNotification,
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
