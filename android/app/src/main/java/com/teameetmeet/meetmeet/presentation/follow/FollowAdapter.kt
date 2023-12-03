package com.teameetmeet.meetmeet.presentation.follow

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus


class FollowAdapter(
    private val actionType: FollowActionType,
    private val userClickListener: OnUserClickListener,
    private val id: Int
) :
    ListAdapter<UserWithFollowStatus, FollowViewHolder>(ItemDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        return FollowViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        holder.bind(getItem(position), actionType, userClickListener, id)
    }

    object ItemDiffCallback : DiffUtil.ItemCallback<UserWithFollowStatus>() {
        override fun areItemsTheSame(
            oldItem: UserWithFollowStatus,
            newItem: UserWithFollowStatus
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: UserWithFollowStatus,
            newItem: UserWithFollowStatus
        ): Boolean {
            return oldItem == newItem
        }
    }
}