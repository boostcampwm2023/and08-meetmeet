package com.teameetmeet.meetmeet.presentation.setting.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingAccountBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingAccountFragment :
    BaseFragment<FragmentSettingAccountBinding>(R.layout.fragment_setting_account) {

    private val viewModel: SettingAccountViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTopAppBar()
        setNavigation()
        setDialog()

    private fun setDialog() {
        binding.settingAccountBtnAccountDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity())
                .setTitle(resources.getString(R.string.setting_account_delete_title))
                .setIcon(R.drawable.ic_warning)
                .setMessage(resources.getString(R.string.setting_account_delete_description))
                .setNegativeButton(resources.getString(R.string.setting_account_delete_cancel)) { _, _ ->
                    // Respond to negative button press
                }
                .setPositiveButton(resources.getString(R.string.setting_account_delete_approve)) { dialog, which ->
                    viewModel.deleteUser()
                }
                .show()
        }
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