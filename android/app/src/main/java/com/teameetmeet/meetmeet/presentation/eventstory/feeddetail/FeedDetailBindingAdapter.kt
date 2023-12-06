package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.EventAuthority

@BindingAdapter("sub_list")
fun <T, VH : RecyclerView.ViewHolder> ViewPager2.bindAdapter(listData: List<T>?) {
    listData ?: return
    (this.adapter as ListAdapter<T, VH>).submitList(listData)
}

@BindingAdapter("authority", "is_mine")
fun MaterialToolbar.bindMenuEnable(eventAuthority: EventAuthority, isMine: Boolean) {
    menu.findItem(R.id.menu_delete_feed_detail).isVisible =
        isMine || eventAuthority == EventAuthority.OWNER
}