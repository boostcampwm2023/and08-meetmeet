package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.net.Uri
import androidx.annotation.OptIn
import androidx.databinding.BindingAdapter
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.feedcontentmedia.FeedContentAdapter
import com.teameetmeet.meetmeet.presentation.model.EventAuthority

@BindingAdapter("sub_list")
fun ViewPager2.bindAdapter(listData: List<Content>?) {
    listData ?: return
    (this.adapter as FeedContentAdapter).submitList(listData)
}

@BindingAdapter("authority", "is_mine")
fun MaterialToolbar.bindMenuEnable(eventAuthority: EventAuthority, isMine: Boolean) {
    menu.findItem(R.id.menu_delete_feed_detail).isVisible =
        isMine || eventAuthority == EventAuthority.OWNER
}

@OptIn(UnstableApi::class)
@BindingAdapter("video_url")
fun PlayerView.bindVideo(videoUrl: String?) {
    (player as ExoPlayer).setMediaItem(
        MediaItem.Builder()
            .setCustomCacheKey(videoUrl)
            .setUri(Uri.parse(videoUrl))
            .build()
    )
}