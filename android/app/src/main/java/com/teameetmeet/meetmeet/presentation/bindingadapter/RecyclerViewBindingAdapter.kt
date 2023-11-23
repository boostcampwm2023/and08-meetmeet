package com.teameetmeet.meetmeet.presentation.bindingadapter


import android.graphics.Rect
import android.view.ContextThemeWrapper
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.util.convertDpToPx


@BindingAdapter("sub_list")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.bindAdapter(listData: List<T>) {
    (this.adapter as ListAdapter<T, VH>).submitList(listData)
}

@BindingAdapter("sub_list", "limit")
fun <T, VH : RecyclerView.ViewHolder> RecyclerView.bindAdapterWithLimit(
    listData: List<T>,
    limit: Int = 0
) {
    val newLimit = if (limit > listData.size) listData.size else limit
    val list = if (newLimit == 0) listData else listData.subList(0, newLimit)
    (this.adapter as ListAdapter<T, VH>).submitList(list)
}

@BindingAdapter("is_expanded")
fun RecyclerView.bindOffset(isExpanded: Boolean) {
    if (itemDecorationCount >= 1) {
        removeItemDecorationAt(0)
    }
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view);
            if (position != 0)
                outRect.left = if (isExpanded) 0 else -convertDpToPx(view.context, 30)
        }
    })
}

@BindingAdapter("count_of_span")
fun RecyclerView.bindGridSpan(spanCount: Int) {
    addItemDecoration(object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount + 1

            if (position >= spanCount) {
                outRect.top = 20
            }
            if (column != 1) {
                outRect.left = 20
            }
        }
    })
}

@BindingAdapter("add_divider")
fun RecyclerView.addDivider(addDivider: Boolean) {
    if (!addDivider) return
    if (layoutManager is LinearLayoutManager) {
        addItemDecoration(
            DividerItemDecoration(
                ContextThemeWrapper(this.context, R.style.Theme_MeetMeetApp),
                (layoutManager as LinearLayoutManager).orientation
            )
        )
    }
    if(layoutManager is GridLayoutManager) {
        addItemDecoration(
            DividerItemDecoration(
                ContextThemeWrapper(this.context, R.style.Theme_MeetMeetApp),
                DividerItemDecoration.HORIZONTAL
            )
        )
        addItemDecoration(
            DividerItemDecoration(
                ContextThemeWrapper(this.context, R.style.Theme_MeetMeetApp),
                DividerItemDecoration.VERTICAL
            )
        )
    }
}