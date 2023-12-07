package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import android.view.View
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.recyclerview.widget.RecyclerView
import com.teameetmeet.meetmeet.data.model.Content
import com.teameetmeet.meetmeet.databinding.ItemFeedContentVideoBinding

class FeedContentsVideoViewHolder(
    val binding: ItemFeedContentVideoBinding,
    private val contentEventListener: ContentEventListener
) : RecyclerView.ViewHolder(binding.root) {

    @OptIn(UnstableApi::class)
    fun bind(data: Content) {
        itemView.setOnClickListener {
            contentEventListener.onClick()
        }
        val player = contentEventListener.getPlayer(absoluteAdapterPosition)

        binding.itemEventFeedPv.player = player
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
            setMediaSource(contentEventListener.getMediaSource(data.path))
            prepare()
        }
    }
}