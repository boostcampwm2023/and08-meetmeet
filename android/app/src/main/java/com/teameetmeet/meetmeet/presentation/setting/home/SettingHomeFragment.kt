package com.teameetmeet.meetmeet.presentation.setting.home

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingHomeBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingHomeFragment :
    BaseFragment<FragmentSettingHomeBinding>(R.layout.fragment_setting_home) {

    private val viewModel: SettingHomeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBinding()
        setTopAppBar()
        setNavigation()
        collectViewModelEvent()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchUserProfile()
    }

    private fun setBinding() {
        binding.vm = viewModel
    }

    private fun setNavigation() {
        binding.settingHomeBtnAccountSetting.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            findNavController().navigate(
                SettingHomeFragmentDirections.actionSettingHomeFragmentToSettingAccountFragment()
            )
        }
        
        binding.settingHomeBtnProfileSetting.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            findNavController().navigate(
                SettingHomeFragmentDirections.actionSettingHomeFragmentToSettingProfileFragment()
            )
        }

        binding.settingHomeBtnAlarmSetting.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            navigateToNotificationSetting()
        }
    }

    private fun navigateToNotificationSetting() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            showMessage(R.string.setting_notification_navigate_fail, e.message.orEmpty())
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is SettingHomeUiEvent.NavigateToLoginActivity -> navigateToLoginActivity()
                        is SettingHomeUiEvent.ShowMessage -> showMessage(
                            event.messageId,
                            event.extraMessage
                        )
                    }
                }
            }
        }
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            requireActivity().finish()
        }
    }
}