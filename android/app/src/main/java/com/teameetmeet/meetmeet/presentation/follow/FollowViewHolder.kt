package com.teameetmeet.meetmeet.presentation.follow

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.UserProfile
import com.teameetmeet.meetmeet.databinding.ItemFollowBinding

class FollowViewHolder private constructor(private val binding: ItemFollowBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(
        user: UserProfile,
        actionType: FollowActionType,
        userClickListener: OnUserClickListener,
        id: Int
    ) {
        binding.user = user
        when (actionType) {
            // todo 타입 별로 버튼에 표시 될 text 추가 필요
            FollowActionType.FOLLOW -> {
                // todo 팔,언팔 구분 필요
                binding.followBtnAction.setOnClickListener {
                    userClickListener.onFollowClick(user)
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