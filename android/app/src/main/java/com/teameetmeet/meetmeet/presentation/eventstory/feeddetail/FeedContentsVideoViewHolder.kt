package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.databinding.ItemFeedContentVideoBinding

class FeedContentsVideoViewHolder(
    val binding: ItemFeedContentVideoBinding,
    private val contentEventListener: ContentEventListener
) : RecyclerView.ViewHolder(binding.root) {
    private val player = ExoPlayer.Builder(itemView.context).build()
    private val dataSourceFactory = DefaultDataSource.Factory(itemView.context)

    @OptIn(UnstableApi::class)
    fun bind(data: Content) {
        binding.itemEventFeedPv.player = player

        itemView.setOnClickListener {
            contentEventListener.onClick()
        }

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(data.path)))

        with(player) {
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaSource(mediaSource)
            prepare()
        }

        contentEventListener.onVideoPrepared(player, absoluteAdapterPosition)
    }
}