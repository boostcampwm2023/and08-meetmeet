package com.teameetmeet.meetmeet.presentation.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.UserWithFollowStatus
import com.teameetmeet.meetmeet.databinding.ItemFollowBinding

class FollowViewHolder private constructor(private val binding: ItemFollowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        user: UserWithFollowStatus,
        actionType: FollowActionType,
        userClickListener: OnUserClickListener,
        id: Int
    ) {
        binding.user = user
        when (actionType) {
            FollowActionType.FOLLOW -> {
                with(binding.followBtnAction) {
                    if (user.isFollowed) {
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

            FollowActionType.EVENT -> {
                binding.followBtnAction.setOnClickListener {
                    userClickListener.onInviteEventClick(user, id)
                }
            }

            FollowActionType.GROUP -> {
                binding.followBtnAction.setOnClickListener {
                    userClickListener.onInviteGroupClick(user, id)
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