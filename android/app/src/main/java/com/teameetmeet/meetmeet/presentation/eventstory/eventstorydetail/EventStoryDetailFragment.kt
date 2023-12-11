package com.teameetmeet.meetmeet.presentation.eventstory.eventstorydetail

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.core.util.Pair
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
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.toDateString
import com.teameetmeet.meetmeet.util.date.toTimeStampLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.ZoneId

@AndroidEntryPoint
class EventStoryDetailFragment :
    BaseFragment<FragmentEventStoryDetailBinding>(R.layout.fragment_event_story_detail) {

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
        setClickListener()
    }

    private fun fetchStoryDetail() {
        viewModel.fetchEventId(args.storyId)
        viewModel.fetchStoryDetail()
    }

    private fun setTopAppBar() {
        binding.storyDetailMtb.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
        binding.storyDetailMtb.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.menu_save -> {
                    if (viewModel.uiState.value.isRepeatEvent) {
                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.story_detail_edit_event_dialog_title)
                            .setMessage(R.string.story_detail_edit_event_dialog_message)
                            .setPositiveButton(R.string.story_detail_edit_event_dialog_description_delete_all) { _, _ ->
                                viewModel.editEvent(isAll = true)
                            }
                            .setNegativeButton(R.string.story_detail_delete_event_dialog_description_delete_cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setNeutralButton(R.string.story_detail_edit_event_dialog_description_delete_one) { _, _ ->
                                viewModel.editEvent(isAll = false)
                            }.show()
                    } else {
                        viewModel.editEvent(isAll = false)
                    }
                }
            }
            true
        }
    }

    private fun setClickListener() {
        binding.storyDetailBtnRemoveEvent.setOnClickListener {
            if (viewModel.uiState.value.isRepeatEvent) {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.story_detail_delete_event_dialog_title)
                    .setMessage(R.string.story_detail_delete_event_dialog_message)
                    .setPositiveButton(R.string.story_detail_delete_event_dialog_description_delete_all) { _, _ ->
                        viewModel.deleteEvent(isAll = true)
                    }
                    .setNegativeButton(R.string.story_detail_delete_event_dialog_description_delete_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setNeutralButton(R.string.story_detail_delete_event_dialog_description_delete_one) { _, _ ->
                        viewModel.deleteEvent(isAll = false)
                    }.show()
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.story_detail_delete_event_confirm_dialog_title)
                    .setMessage(R.string.story_detail_delete_event_confirm_dialog_message)
                    .setPositiveButton(R.string.story_detail_delete_event_confirm_dialog_description_delete) { _, _ ->
                        viewModel.deleteEvent(isAll = false)
                    }
                    .setNegativeButton(R.string.story_detail_delete_event_dialog_description_delete_cancel) { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            }
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is EventStoryDetailEvent.ShowMessage -> showMessage(
                            event.messageId,
                            event.extraMessage
                        )

                        is EventStoryDetailEvent.FinishEventStoryActivity -> requireActivity().finish()
                        is EventStoryDetailEvent.NavigateToLoginActivity -> navigateToLoginActivity()
                        is EventStoryDetailEvent.FinishEventStoryDetail -> findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setBinding() {
        binding.vm = viewModel
    }

    private fun setRepeatOptions() {
        binding.storyDetailEtEventRepeat.setOnItemClickListener { _, _, index, _ ->
            viewModel.setEventRepeat(index)
        }
        binding.storyDetailEtEventRepeatFrequency.setOnItemClickListener { _, _, index, _ ->
            viewModel.setEventRepeatFrequency(index)
        }
    }

    private fun setNotificationOptions() {
        val items = EventNotification.entries.map { getString(it.stringResId) }.toTypedArray()
        (binding.storyDetailTilEventAlarm.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            items
        )
        binding.storyDetailEtEventAlarm.setOnItemClickListener { _, _, index, _ ->
            viewModel.setEventAlarm(index)
        }
    }

    private fun setDateTimePicker() {


        binding.storyDetailTvValueStartDate.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText(getString(R.string.add_event_title))
                    .setSelection(
                        Pair(
                            viewModel.uiState.value.startDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC")),
                            viewModel.uiState.value.endDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC"))
                        )
                    ).build()

            dateRangePicker.addOnPositiveButtonClickListener {
                viewModel.setEventDate(
                    it.first.toDateString(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC")),
                    it.second.toDateString(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC"))
                )
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "DateRangePicker")
        }
        binding.storyDetailTvValueEndDate.setOnClickListener {val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.add_event_title))
                .setSelection(
                    Pair(
                        viewModel.uiState.value.startDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC")),
                        viewModel.uiState.value.endDate.toTimeStampLong(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC"))
                    )
                ).build()

            dateRangePicker.addOnPositiveButtonClickListener {
                viewModel.setEventDate(
                    it.first.toDateString(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC")),
                    it.second.toDateString(DateTimeFormat.LOCAL_DATE, ZoneId.of("UTC"))
                )
            }
            dateRangePicker.show(requireActivity().supportFragmentManager, "DateRangePicker")

        }

        binding.eventStoryTvValueEventRepeatEndDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.story_detail_description_event_repeat_end_date))
                .setSelection(viewModel.uiState.value.eventRepeatEndDate?.toTimeStampLong(DateTimeFormat.LOCAL_DATE))
                .build()
            datePicker.show(requireActivity().supportFragmentManager, null)
            datePicker.addOnPositiveButtonClickListener {
                viewModel.setRepeatEndDate(it)
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