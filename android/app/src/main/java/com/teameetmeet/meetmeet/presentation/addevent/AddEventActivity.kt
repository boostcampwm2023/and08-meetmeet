package com.teameetmeet.meetmeet.presentation.addevent

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.activity.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityAddEventBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEventActivity : BaseActivity<ActivityAddEventBinding>(R.layout.activity_add_event) {

    private val viewModel: AddEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.vm = viewModel

        setTopAppBar()
        setTextChangeListener()
        setDatePicker()
        setTimePicker()

    private fun setTopAppBar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_save -> {
                    viewModel.eventSave()
                    true
                }

                else -> false
            }
        }
    }

    private fun setTextChangeListener() {
        binding.etEventName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setEventName(binding.etEventName.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                binding.etEventName.text?.let { name ->
                    if (name.isEmpty()) {
                        binding.tfEventName.error = "일정 이름을 입력해 주세요."
                    } else {
                        binding.tfEventName.error = null
                    }
                }
            }
        })

        binding.etEventMemo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.setEventMemo(binding.etEventMemo.text.toString())
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.dateRangePicker().setTitleText(getString(R.string.add_event))
                .setSelection(
                    viewModel.eventDate.value
                ).build()

        datePicker.addOnPositiveButtonClickListener {
            viewModel.setEventDate(it.first, it.second)
        }

        binding.etEventStartDate.setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_UP) {
                datePicker.show(supportFragmentManager, "DatePicker")
            }
            true
        }
        binding.etEventEndDate.setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_UP) {
                datePicker.show(supportFragmentManager, "DatePicker")
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTimePicker() {
        val startTimePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(viewModel.eventStartTime.value.hour)
            .setMinute(viewModel.eventStartTime.value.minute).setTitleText("시작 시간을 지정해 주세요.")
            .build()

        val endTimePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(viewModel.eventEndTime.value.hour)
            .setMinute(viewModel.eventEndTime.value.minute).setTitleText("종료 시간을 지정해 주세요.").build()

        binding.etEventStartTime.setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_UP) {
                startTimePicker.show(supportFragmentManager, "StartTimePicker")
            }
            true
        }

        startTimePicker.addOnPositiveButtonClickListener {
            viewModel.setEventStartTime(startTimePicker.hour, startTimePicker.minute)
        }

        binding.etEventEndTime.setOnTouchListener { _, e ->
            if (e.action == MotionEvent.ACTION_UP) {
                endTimePicker.show(supportFragmentManager, "EndTimePicker")
            }
            true
        }

        endTimePicker.addOnPositiveButtonClickListener {
            viewModel.setEventEndTime(endTimePicker.hour, endTimePicker.minute)
        }
    }
    }
}