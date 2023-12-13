package com.teameetmeet.meetmeet.presentation.follow

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.teameetmeet.meetmeet.data.model.UserStatus


class FollowAdapter(
    private val actionType: FollowActionType,
    private val userClickListener: OnUserClickListener,
    private val eventId: Int?
) : ListAdapter<UserStatus, FollowViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        return FollowViewHolder.from(parent, actionType, userClickListener, eventId)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<UserStatus>() {
            override fun areItemsTheSame(
                oldItem: UserStatus, newItem: UserStatus
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UserStatus, newItem: UserStatus
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}