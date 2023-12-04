package com.teameetmeet.meetmeet.presentation.follow

import android.os.Bundle

sealed class FollowState {

    abstract val value: Boolean

    data object Following : FollowState() {
        override val value = true
    }

    data object Follower : FollowState() {
        override val value = false
    }

    fun toBundle(): Bundle {
        return when (this) {
            is Following -> Bundle().apply { putBoolean("state", this@FollowState.value) }
            is Follower -> Bundle().apply { putBoolean("state", this@FollowState.value) }
        }
    }

    companion object {
        fun fromBundle(bundle: Bundle): FollowState {
            return if (bundle.getBoolean("state")) {
                Following
            } else {
                Follower
            }
        }
    }
}

enum class FollowActionType {
    FOLLOW,
    EVENT,
    GROUP,
}