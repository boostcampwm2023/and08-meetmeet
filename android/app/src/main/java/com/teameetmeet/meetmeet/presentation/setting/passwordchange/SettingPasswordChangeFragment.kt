package com.teameetmeet.meetmeet.presentation.setting.passwordchange

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingPasswordChangeBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingPasswordChangeFragment :
    BaseFragment<FragmentSettingPasswordChangeBinding>(R.layout.fragment_setting_password_change) {

    private val viewModel: SettingPasswordChangeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel

        setTopAppBar()
        collectViewModelEvent()
    }

    private fun setTopAppBar() {
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is SettingPasswordChangeUiEvent.NavigateToSettingHomeFragment -> {
                            findNavController().popBackStack()
                        }

                        is SettingPasswordChangeUiEvent.ShowMessage -> {
                            showMessage(event.message, event.extraMessage)
                        }

                        is SettingPasswordChangeUiEvent.NavigateToLoginActivity -> {
                            navigateToLoginActivity()
                        }
                    }
                }
            }
        }
    }
}