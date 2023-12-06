package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import com.teameetmeet.meetmeet.data.model.Comment

interface CommentDeleteClickListener {
    fun onClick(comment: Comment)
}