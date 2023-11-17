package com.teameetmeet.meetmeet.presentation.setting.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingHomeBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingHomeFragment :
    BaseFragment<FragmentSettingHomeBinding>(R.layout.fragment_setting_home) {

    private val viewModel : SettingHomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setTopAppBar()
        setNavigation()
    }

    private fun setBinding() {
        binding.vm = viewModel
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