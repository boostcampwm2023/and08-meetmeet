package com.teameetmeet.meetmeet.presentation.setting.home

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingHomeBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingHomeFragment :
    BaseFragment<FragmentSettingHomeBinding>(R.layout.fragment_setting_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTopAppBar()
        setNavigation()
    }

    private fun setNavigation() {
        binding.settingHomeBtnAccountSetting.setOnClickListener {
            findNavController().navigate(
                SettingHomeFragmentDirections.actionSettingHomeFragmentToSettingAccountFragment()
            )
        }
        binding.settingHomeBtnProfileSetting.setOnClickListener {
            findNavController().navigate(
                SettingHomeFragmentDirections.actionSettingHomeFragmentToSettingProfileFragment()
            )
        }
        binding.settingHomeBtnAlarmSetting.setOnClickListener {
            findNavController().navigate(
                SettingHomeFragmentDirections.actionSettingHomeFragmentToSettingAlarmFragment()
            )
        }
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}