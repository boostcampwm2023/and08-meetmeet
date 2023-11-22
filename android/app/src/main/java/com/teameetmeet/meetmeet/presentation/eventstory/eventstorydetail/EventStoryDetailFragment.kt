package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventStoryDetailBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm

class EventStoryDetailFragment : BaseFragment<FragmentEventStoryDetailBinding>(R.layout.fragment_event_story_detail) {

    private val viewModel: EventStoryDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setNotificationOptions()
        setRepeatOptions()
        setDateTimePicker()
    }

    private fun setBinding() {
        binding.vm = viewModel
    }

    private fun setRepeatOptions() {
        val items = EventRepeatTerm.entries.map{getString(it.stringResId)}.toTypedArray()
        (binding.storyDetailTilEventRepeat.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)
        binding.storyDetailEtEventRepeat.setOnItemClickListener { _, _, index, _ ->
            viewModel.setEventRepeat(index)
        }
    }

    private fun setNotificationOptions() {
        val items = EventNotification.entries.map{getString(it.stringResId)}.toTypedArray()
        (binding.storyDetailTilEventAlarm.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)
        binding.storyDetailEtEventAlarm.setOnItemClickListener { _, _, index, _ ->
            viewModel.setEventAlarm(index)
        }
    }

    private fun setDateTimePicker() {
        binding.storyDetailTvValueStartDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.story_detail_description_start_date)).build()
            datePicker.show(requireActivity().supportFragmentManager, null)
            datePicker.addOnPositiveButtonClickListener {
                viewModel.setEventStartDate(it)
            }
        }
        binding.storyDetailTvValueEndDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.story_detail_description_start_date)).build()
            datePicker.show(requireActivity().supportFragmentManager, null)
            datePicker.addOnPositiveButtonClickListener {
                viewModel.setEventEndDate(it)
            }
        }
        binding.storyDetailTvValueStartTime.setOnClickListener {

        }
        binding.storyDetailTvValueEndTime.setOnClickListener {

        }
    }
}