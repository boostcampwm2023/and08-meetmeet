package com.teameetmeet.meetmeet.presentation.calendar

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.presentation.model.CalendarItem
import com.teameetmeet.meetmeet.util.toYearMonth
import java.time.LocalDate

@BindingAdapter("local_date")
fun bindLocalDate(textView: TextView, localDate: LocalDate) {
    textView.text = localDate.toYearMonth()
}

@BindingAdapter("day_list", "vm")
fun bindDayInMonth(
    recyclerView: RecyclerView,
    dayList: List<CalendarItem>,
    viewModel: CalendarViewModel
) {
    if (recyclerView.adapter == null) {
        recyclerView.adapter = CalendarAdapter(viewModel)
    }
    (recyclerView.adapter as CalendarAdapter).submitList(dayList)
}

@BindingAdapter("image")
fun bindUserProfileImage(
    imageView: ImageView,
    profileImage: String
) {
    Log.d("test", profileImage)
    Glide.with(imageView.context).load(profileImage)
        .centerCrop().fallback(R.drawable.ic_plus).error(R.drawable.ic_follow)
        .placeholder(R.drawable.ic_person).into(imageView)
}