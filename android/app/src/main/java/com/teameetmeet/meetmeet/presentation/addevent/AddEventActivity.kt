package com.teameetmeet.meetmeet.presentation.addevent

import android.os.Bundle
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
    }
}