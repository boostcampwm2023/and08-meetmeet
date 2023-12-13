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
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
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
        collectViewModelEvent()
    }

    private fun collectViewModelEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is SettingAccountUiEvent.NavigateToLoginActivity -> {
                            findNavController().navigate(
                                SettingAccountFragmentDirections.actionSettingAccountFragmentToLoginActivity()
                            )
                            requireActivity().finishAffinity()
                        }

                        is SettingAccountUiEvent.ShowMessage -> showMessage(
                            event.message,
                            event.extraMessage
                        )
                    }
                }
            }
        }
    }

    private fun setDialog() {
        binding.settingAccountBtnAccountDelete.setClickEvent(viewLifecycleOwner.lifecycleScope) {
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
        binding.settingAccountBtnPasswordChange.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            findNavController().navigate(
                SettingAccountFragmentDirections.actionSettingAccountFragmentToSettingPasswordChangeFragment()
            )
        }
    }
}