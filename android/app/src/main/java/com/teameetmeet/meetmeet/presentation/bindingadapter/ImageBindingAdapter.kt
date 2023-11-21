package com.teameetmeet.meetmeet.presentation.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.teameetmeet.meetmeet.R

@BindingAdapter("image")
fun bindUserProfileImage(
    imageView: ImageView,
    profileImage: String?
) {
    Glide.with(imageView.context).load(profileImage)
        .centerCrop().fallback(R.drawable.ic_plus).error(R.drawable.ic_follow)
        .placeholder(R.drawable.ic_person).into(imageView)
}