package com.teameetmeet.meetmeet.presentation.eventstory.eventstory

import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.EventMember
import com.teameetmeet.meetmeet.data.model.EventStory
import com.teameetmeet.meetmeet.data.model.Feed
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventFeedListAdapter
import com.teameetmeet.meetmeet.presentation.eventstory.eventstory.adapter.EventMemberListAdapter
import com.teameetmeet.meetmeet.util.convertDpToPx

@BindingAdapter("event_story")
fun bindTextViewDate(textView: TextView, eventStory: EventStory?) {
    eventStory ?: return
    textView.text = String.format(textView.context.getString(R.string.event_story_event_date), eventStory.startDate, eventStory.endDate)
}

@BindingAdapter("event_members")
fun bindRecyclerViewEventMember(recyclerView: RecyclerView, eventMembers: List<EventMember>) {
    (recyclerView.adapter as EventMemberListAdapter).submitList(eventMembers)
}

@BindingAdapter("event_feeds")
fun bindRecyclerViewEventFeed(recyclerView: RecyclerView, feeds: List<Feed>) {
    Log.d("test", feeds.toString())
    (recyclerView.adapter as EventFeedListAdapter).submitList(feeds)
}

@BindingAdapter("is_expanded")
fun bindRecyclerViewIsExpanded(recyclerView: RecyclerView, isExpanded: Boolean) {
    if(recyclerView.itemDecorationCount >= 1) {
        recyclerView.removeItemDecorationAt(0)
    }
    recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration(){
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