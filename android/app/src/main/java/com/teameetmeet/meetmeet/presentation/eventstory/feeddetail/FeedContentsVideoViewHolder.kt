package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.databinding.ItemFeedContentVideoBinding

class FeedContentsVideoViewHolder(
    val binding: ItemFeedContentVideoBinding,
    private val contentEventListener: ContentEventListener
) : RecyclerView.ViewHolder(binding.root) {
    private val player = ExoPlayer.Builder(itemView.context).build()

    fun bind(data: Content) {
        binding.itemEventFeedPv.player = player

        itemView.setOnClickListener {
            contentEventListener.onClick()
        }

        with(player) {
            repeatMode = Player.REPEAT_MODE_ONE
            setMediaItem(MediaItem.fromUri(Uri.parse(data.path)))
            prepare()
        }

        contentEventListener.onVideoPrepared(player, absoluteAdapterPosition)
    }
}