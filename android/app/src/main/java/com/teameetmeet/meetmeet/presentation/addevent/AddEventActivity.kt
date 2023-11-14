package com.teameetmeet.meetmeet.presentation.addevent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
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
    }
}