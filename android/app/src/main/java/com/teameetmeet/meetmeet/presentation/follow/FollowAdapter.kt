package com.teameetmeet.meetmeet.presentation.follow

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.UserStatus


class FollowAdapter(
    private val actionType: FollowActionType,
    private val userClickListener: OnUserClickListener,
    private val id: Int
) :
    ListAdapter<UserStatus, FollowViewHolder>(ItemDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        return FollowViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        holder.bind(getItem(position), actionType, userClickListener, id)
    }

    object ItemDiffCallback : DiffUtil.ItemCallback<UserStatus>() {
        override fun areItemsTheSame(
            oldItem: UserStatus,
            newItem: UserStatus
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: UserStatus,
            newItem: UserStatus
        ): Boolean {
            return oldItem == newItem
        }
    }
}