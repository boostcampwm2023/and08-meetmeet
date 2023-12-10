package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail.temp

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.teameetmeet.meetmeet.data.model.Content

class FeedContentAdapter(
    fragment: Fragment
) : FragmentStateAdapter(fragment) {
    private var contents = emptyList<Content>()
    private val diffCallback: (List<Content>, List<Content>) -> DiffUtil.Callback =
        { oldList, newList ->
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = oldList.size

                override fun getNewListSize(): Int = newList.size

                override fun areItemsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldList[oldItemPosition].id == newList[newItemPosition].id
                }

                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                    return oldList[oldItemPosition] == newList[newItemPosition]
                }
            }
        }

    override fun getItemCount(): Int = contents.size

    fun submitList(newContents: List<Content>) {
        val diffResult = DiffUtil.calculateDiff(diffCallback(contents, newContents))
        contents = newContents
        diffResult.dispatchUpdatesTo(this)
    }

    override fun createFragment(position: Int): Fragment {
        return when (contents[position].mimeType.startsWith("video")) {
            true -> {
                FeedContentVideoFragment.create(contents[position].path)
            }

            false -> {
                FeedContentImageFragment.create(contents[position].path)
            }
        }
    }
}