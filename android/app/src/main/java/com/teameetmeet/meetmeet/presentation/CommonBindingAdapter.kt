package com.teameetmeet.meetmeet.presentation

import android.graphics.Rect
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.util.convertDpToPx

@BindingAdapter("sub_list")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.bindAdapter(listData: List<T>) {
    (this.adapter as ListAdapter<T, VH>).submitList(listData)
}

@BindingAdapter("is_expanded")
fun RecyclerView.bindOffset(isExpanded: Boolean) {
    if(itemDecorationCount >= 1) {
        removeItemDecorationAt(0)
    }
    addItemDecoration(object : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view);
            if (position != 0)
                outRect.left = if(isExpanded) 0 else -convertDpToPx(view.context, 30)
        }
    })
}

@BindingAdapter("count_of_span")
fun RecyclerView.bindGridSpan(spanCount: Int) {
    addItemDecoration(object : RecyclerView.ItemDecoration(){
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount + 1

            if (position >= spanCount){
                outRect.top = 20
            }
            if (column != 1){
                outRect.left = 20
            }
        }
    })
}