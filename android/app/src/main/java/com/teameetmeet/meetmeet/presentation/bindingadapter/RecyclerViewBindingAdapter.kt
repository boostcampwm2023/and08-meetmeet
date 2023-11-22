package com.teameetmeet.meetmeet.presentation.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("sub_list")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.bindAdapter(listData: List<T>) {
    (this.adapter as ListAdapter<T, VH>).submitList(listData)
}