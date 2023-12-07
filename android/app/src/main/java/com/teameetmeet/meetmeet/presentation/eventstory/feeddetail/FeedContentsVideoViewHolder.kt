package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.net.Uri
import android.view.View
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
            addListener(object : Player.Listener {
                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        binding.itemEventFeedPbContent.visibility = View.VISIBLE
                    } else if (playbackState == Player.STATE_READY) {
                        binding.itemEventFeedPbContent.visibility = View.GONE
                    }
                }
            })
            setMediaSource(mediaSource)
            prepare()
        }

        contentEventListener.onVideoPrepared(player, absoluteAdapterPosition)
    }
}