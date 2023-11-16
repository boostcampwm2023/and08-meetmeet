package com.teameetmeet.meetmeet.presentation.setting.home

import android.os.Bundle
import android.view.View
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingHomeBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingHomeFragment :
    BaseFragment<FragmentSettingHomeBinding>(R.layout.fragment_setting_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}