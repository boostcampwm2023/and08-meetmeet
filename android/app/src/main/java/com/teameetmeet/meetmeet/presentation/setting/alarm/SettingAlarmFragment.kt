package com.teameetmeet.meetmeet.presentation.setting.alarm

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingAlarmBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingAlarmFragment :
    BaseFragment<FragmentSettingAlarmBinding>(R.layout.fragment_setting_alarm) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTopAppBar()
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
}