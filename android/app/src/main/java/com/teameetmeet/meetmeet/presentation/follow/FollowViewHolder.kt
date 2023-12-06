package com.teameetmeet.meetmeet.presentation.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserStatus
import com.teameetmeet.meetmeet.databinding.ItemFollowBinding

class FollowViewHolder private constructor(private val binding: ItemFollowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        user: UserStatus,
        actionType: FollowActionType,
        userClickListener: OnUserClickListener,
        id: Int?
    ) {
        binding.user = user
        itemView.setOnClickListener {
            userClickListener.onProfileClick(user)
        }
        when (actionType) {
            FollowActionType.FOLLOW -> {
                with(binding.followBtnAction) {
                    user.isFollowed?.let { followStatus ->
                        if (followStatus) {
                            text = context.getString(R.string.follow_title_unfollow)
                            setOnClickListener {
                                userClickListener.onUnfollowClick(user)
                            }
                        } else {
                            text = context.getString(R.string.follow_title_follow)
                            setOnClickListener {
                                userClickListener.onFollowClick(user)
                            }
                        }
                    }
                }
            }

            FollowActionType.EVENT -> {
                with(binding.followBtnAction) {
                    when (user.isJoined) {
                        UserStatus.JOIN_STATUS_JOINABLE -> {
                            text = context.getString(R.string.event_story_invite)
                            setOnClickListener {
                                id?.let {
                                    userClickListener.onInviteEventClick(user, id)
                                }
                            }
                            isEnabled = true
                        }

                        UserStatus.JOIN_STATUS_PENDING -> {
                            text = context.getString(R.string.event_story_pending)
                            isEnabled = false
                        }

                        UserStatus.JOIN_STATUS_ACCEPTED -> {
                            text = context.getString(R.string.event_story_participating)
                            isEnabled = false
                        }

                        else -> {
                            isEnabled = false
                        }
                    }
                }
            }

            FollowActionType.GROUP -> {
                binding.followBtnAction.setOnClickListener {
                    id?.let {
                        userClickListener.onInviteGroupClick(user, id)
                    }
                }
            }
        }

    }

    companion object {
        fun from(parent: ViewGroup): FollowViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return FollowViewHolder(ItemFollowBinding.inflate(inflater, parent, false))
        }
    }
}