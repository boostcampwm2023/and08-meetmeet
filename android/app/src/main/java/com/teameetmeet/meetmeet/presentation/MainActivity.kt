package com.teameetmeet.meetmeet.presentation

import android.os.Bundle
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityMainBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}