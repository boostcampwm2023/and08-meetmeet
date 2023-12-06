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

//todo: 속성 이름 바꿔야 할 듯
@BindingAdapter("image_src")
fun ImageView.bindThumbnailImage(
    thumbnailImage: String?
) {
    Glide.with(context).load(thumbnailImage)
        .centerCrop()
        .placeholder(R.drawable.ic_no_image_filled).into(this)
}

@BindingAdapter("center_inside_image")
fun ImageView.bindThumbnailImageCenterInside(
    thumbnailImage: String?
) {
    Glide.with(context).load(thumbnailImage)
        .centerInside()
        .error(R.drawable.ic_no_image_filled)
        .placeholder(R.drawable.loading_image).into(this)
}