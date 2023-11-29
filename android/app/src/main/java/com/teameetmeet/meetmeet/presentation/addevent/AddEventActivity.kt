package com.teameetmeet.meetmeet.presentation.addevent

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityAddEventBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import com.teameetmeet.meetmeet.presentation.model.EventNotification
import com.teameetmeet.meetmeet.presentation.model.EventRepeatTerm
import com.teameetmeet.meetmeet.util.date.toLocalDateTime
import com.teameetmeet.meetmeet.util.date.toLong
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

@AndroidEntryPoint
class AddEventActivity : BaseActivity<ActivityAddEventBinding>(R.layout.activity_add_event) {

    private val viewModel: AddEventViewModel by viewModels()

    private val args: AddEventActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.vm = viewModel

        initDateTime(args.date)
        setTopAppBar()
        setDateTimePicker()
        setNotificationOptions()
        setRepeatOptions()
        collectViewModelEvent()
    }

    private fun initDateTime(date: LocalDate) {
        viewModel.setEventDate(date.atStartOfDay(), date.atStartOfDay())
        viewModel.setRepeatEndDate(date.atStartOfDay().plusYears(1))
    }

    private fun setTopAppBar() {
        with(binding.topAppBar) {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_save -> {
                        viewModel.eventSave()
                        true
                    }

                    else -> false
                }
            }
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun collectViewModelEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is AddEventUiEvent.ShowMessage -> showMessage(
                            event.messageId,
                            event.extraMessage
                        )

                        is AddEventUiEvent.FinishAddEventActivity -> {
                            finish()
                        }
                    }
                }
            }
        }
    }

    private fun setRepeatOptions() {
        val items = EventRepeatTerm.entries.map { getString(it.stringResId) }.toTypedArray()
        (binding.addEventTilEventRepeat.editText as? MaterialAutoCompleteTextView)?.setText(items.first())
        (binding.addEventTilEventRepeat.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            items
        )
        binding.addEventEtEventRepeat.setOnItemClickListener { _, _, index, _ ->
            viewModel.setEventRepeat(index)
        }

        val frequencyItems = arrayOf("1", "2", "3", "4", "5", "6")
        (binding.addEventTilEventRepeatFrequency.editText as? MaterialAutoCompleteTextView)?.setText(
            frequencyItems.first()
        )
        (binding.addEventTilEventRepeatFrequency.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            frequencyItems
        )
        binding.addEventEtEventRepeatFrequency.doAfterTextChanged {
            viewModel.setEventRepeatFrequency(it.toString())
        }
    }

    private fun setNotificationOptions() {
        val items = EventNotification.entries.map { getString(it.stringResId) }.toTypedArray()
        (binding.addEventTilEventAlarm.editText as? MaterialAutoCompleteTextView)?.setText(items.first())
        (binding.addEventTilEventAlarm.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(
            items
        )
        binding.addEventEtEventAlarm.setOnItemClickListener { _, _, index, _ ->
            viewModel.setEventAlarm(index)
        }
    }

    private fun setDateTimePicker() {
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText(getString(R.string.add_event_title))
                .setSelection(
                    Pair(
                        viewModel.uiState.value.startDate.toLong(ZoneId.of("UTC")),
                        viewModel.uiState.value.endDate.toLong(ZoneId.of("UTC"))
                    )
                ).build()

        dateRangePicker.addOnPositiveButtonClickListener {
            viewModel.setEventDate(
                it.first.toLocalDateTime(ZoneId.of("UTC")),
                it.second.toLocalDateTime(ZoneId.of("UTC"))
            )
        }

        binding.addEventTvValueStartDate.setOnClickListener {
            dateRangePicker.show(supportFragmentManager, "DateRangePicker")
        }
        binding.addEventTvValueEndDate.setOnClickListener {
            dateRangePicker.show(supportFragmentManager, "DateRangePicker")
        }
        binding.eventStoryTvValueEventRepeatEndDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.story_detail_description_event_repeat_end_date))
                .build()

            datePicker.addOnPositiveButtonClickListener {
                viewModel.setRepeatEndDate(it.toLocalDateTime(ZoneId.of("UTC")))
            }
            datePicker.show(supportFragmentManager, "DatePicker")
        }

        val startTimePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(viewModel.uiState.value.startTime.hour)
            .setMinute(viewModel.uiState.value.startTime.minute)
            .setTitleText(getString(R.string.add_event_err_start_time))
            .build()

        startTimePicker.addOnPositiveButtonClickListener {
            viewModel.setEventStartTime(startTimePicker.hour, startTimePicker.minute)
        }

        val endTimePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(viewModel.uiState.value.endTime.hour)
            .setMinute(viewModel.uiState.value.endTime.minute)
            .setTitleText(getString(R.string.add_event_err_end_time)).build()

        endTimePicker.addOnPositiveButtonClickListener {
            viewModel.setEventEndTime(endTimePicker.hour, endTimePicker.minute)
        }

        binding.addEventTvValueStartTime.setOnClickListener {
            startTimePicker.show(supportFragmentManager, "StartTimePicker")
        }
        binding.addEventTvValueEndTime.setOnClickListener {
            endTimePicker.show(supportFragmentManager, "EndTimePicker")
        }
    }
}