package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

@BindingAdapter("sub_list")
fun <T, VH : RecyclerView.ViewHolder> ViewPager2.bindAdapter(listData: List<T>?) {
    listData ?: return
    (this.adapter as ListAdapter<T, VH>).submitList(listData)
}