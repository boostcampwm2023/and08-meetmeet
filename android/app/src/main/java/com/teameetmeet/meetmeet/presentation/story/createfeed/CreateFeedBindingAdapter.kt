package com.teameetmeet.meetmeet.presentation.story.createfeed

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.MediaItem

@BindingAdapter("image_src")
fun ImageView.bindImage(
    uri: Uri
) {
    Glide.with(this).load(uri)
        .placeholder(R.drawable.ic_no_image_filled)
        .centerCrop()
        .into(this)
}

@BindingAdapter("uris", "listener")
fun RecyclerView.submitList(
    list: List<MediaItem>,
    listener: MediaItemCancelClickListener
) {
    if (adapter == null) adapter = MediaAdapter(listener)
    (adapter as MediaAdapter).submitList(list)
}