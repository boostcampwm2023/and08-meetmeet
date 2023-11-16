package com.teameetmeet.meetmeet.presentation.setting.account

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingAccountBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingAccountFragment :
    BaseFragment<FragmentSettingAccountBinding>(R.layout.fragment_setting_account) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTopAppBar()
        setNavigation()
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setNavigation() {
        binding.settingAccountBtnPasswordChange.setOnClickListener {
            findNavController().navigate(
                SettingAccountFragmentDirections.actionSettingAccountFragmentToSettingPasswordChangeFragment()
            )
        }
    }
}