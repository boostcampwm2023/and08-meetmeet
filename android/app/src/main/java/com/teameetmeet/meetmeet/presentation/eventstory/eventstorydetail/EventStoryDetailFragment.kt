package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEventStoryDetailBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EventStoryDetailFragment : BaseFragment<FragmentEventStoryDetailBinding>(R.layout.fragment_event_story_detail) {

    private val viewModel: EventStoryDetailViewModel by viewModels()
    private val args: EventStoryDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        fetchStoryDetail()
        setNotificationOptions()
        setRepeatOptions()
        setDateTimePicker()
        collectViewModelEvent()
        setTopAppBar()
    }

    private fun fetchStoryDetail() {
        viewModel.fetchEventId(args.storyId)
        viewModel.fetchStoryDetail()
    }

    private fun setTopAppBar() {
        binding.storyDetailMtb.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when(event) {
                        is EventStoryDetailEvent.ShowMessage -> showMessage(event.messageId, event.extraMessage)
                        is EventStoryDetailEvent.FinishEventStoryActivity -> requireActivity().finish()
                    }
                }
            }
        }
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
            val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText(getString(R.string.story_detail_description_end_date)).build()
            datePicker.show(requireActivity().supportFragmentManager, null)
            datePicker.addOnPositiveButtonClickListener {
                viewModel.setEventEndDate(it)
            }
        }
        binding.storyDetailTvValueStartTime.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(viewModel.uiState.value.startTime.hour)
                    .setMinute(viewModel.uiState.value.startTime.minute)
                    .setTitleText(getString(R.string.story_deatil_description_start_time))
                    .build()
            picker.show(requireActivity().supportFragmentManager, null)
            picker.addOnPositiveButtonClickListener {
                viewModel.setEventStartTime(picker.hour, picker.minute)
            }
        }
        binding.storyDetailTvValueEndTime.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(viewModel.uiState.value.endTime.hour)
                .setMinute(viewModel.uiState.value.endTime.minute)
                .setTitleText(getString(R.string.story_detail_description_end_time))
                .build()
            picker.show(requireActivity().supportFragmentManager, null)
            picker.addOnPositiveButtonClickListener {
                viewModel.setEventEndTime(picker.hour, picker.minute)
            }
        }
    }
}