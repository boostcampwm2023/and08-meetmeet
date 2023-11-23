package com.teameetmeet.meetmeet.presentation.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.teameetmeet.meetmeet.R

@BindingAdapter("image")
fun ImageView.bindUserProfileImage(
    profileImage: String?
) {
    Glide.with(context).load(profileImage)
        .centerCrop()
        .placeholder(R.drawable.ic_person).into(this)
}