package com.teameetmeet.meetmeet.presentation.addevent

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.activity.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.data.model.EventNotification
import com.teameetmeet.meetmeet.data.model.EventRepeatTerm
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
        setNotificationOptions()
        setRepeatTermOptions()
    }

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
                        binding.tfEventName.error = getString(R.string.add_event_err_event_name)
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
            MaterialDatePicker.Builder.dateRangePicker().setTitleText(getString(R.string.add_event_title))
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
            .setMinute(viewModel.eventStartTime.value.minute).setTitleText(getString(R.string.add_event_err_start_time))
            .build()

        val endTimePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(viewModel.eventEndTime.value.hour)
            .setMinute(viewModel.eventEndTime.value.minute).setTitleText(getString(R.string.add_event_err_end_time)).build()

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

    private fun setNotificationOptions() {
        val notificationOptions = EventNotification.values()
        val adapter = object : ArrayAdapter<EventNotification>(
            this, R.layout.item_text_field, notificationOptions
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                getItem(position)?.let {
                    (view as TextView).text = getString(it.stringResId)
                }
                return view
            }
        }

        (binding.tfEventAlarm.editText as? AutoCompleteTextView)?.setText(
            getString(viewModel.eventNotification.value.stringResId), false
        )

        (binding.tfEventAlarm.editText as? AutoCompleteTextView)?.let { tv ->
            tv.setAdapter(adapter)
            adapter.setDropDownViewResource(R.layout.item_text_field)

            tv.setOnItemClickListener { _, _, position, _ ->
                val selectedNotification = adapter.getItem(position)
                selectedNotification?.let {
                    viewModel.setEventNotification(it)
                    val selectedNotificationText = getString(it.stringResId)
                    tv.setText(selectedNotificationText, false)
                }
            }
        }
    }

    private fun setRepeatTermOptions() {
        val repeatTermOptions = EventRepeatTerm.values()
        val adapter = object : ArrayAdapter<EventRepeatTerm>(
            this, R.layout.item_text_field, repeatTermOptions
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                getItem(position)?.let {
                    (view as TextView).text = getString(it.stringResId)
                }
                return view
            }
        }

        (binding.tfEventRepeat.editText as? AutoCompleteTextView)?.setText(
            getString(viewModel.eventRepeatTerm.value.stringResId), false
        )

        (binding.tfEventRepeat.editText as? AutoCompleteTextView)?.let { tv ->
            tv.setAdapter(adapter)
            adapter.setDropDownViewResource(R.layout.item_text_field)

            tv.setOnItemClickListener { _, _, position, _ ->
                val selectedRepeatOption = adapter.getItem(position)
                selectedRepeatOption?.let {
                    viewModel.setEventRepeatTerm(it)
                    val selectedNotificationText = getString(it.stringResId)
                    tv.setText(selectedNotificationText, false)
                }
            }
        }
    }
}