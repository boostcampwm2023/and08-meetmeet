package com.teameetmeet.meetmeet.presentation.setting.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingProfileBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingProfileFragment :
    BaseFragment<FragmentSettingProfileBinding>(R.layout.fragment_setting_profile) {

    private val args: SettingProfileFragmentArgs by navArgs()
    private lateinit var callback: OnBackPressedCallback
    private val viewModel: SettingProfileViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (args.isFirstSignIn) {
            callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigate(SettingProfileFragmentDirections.actionSettingProfileFragmentToHomeActivity())
                    requireActivity().finish()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.vm = viewModel
        setTopAppBar(args.isFirstSignIn)
        setPhotoPicker()
        collectViewModelEvent(args.isFirstSignIn)
    }

    private fun collectViewModelEvent(isFirstSignIn: Boolean) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is SettingProfileUiEvent.NavigateToSettingHomeFragment -> {
                            if (isFirstSignIn) {
                                findNavController().navigate(SettingProfileFragmentDirections.actionSettingProfileFragmentToHomeActivity())
                                requireActivity().finish()
                            } else {
                                findNavController().popBackStack()
                            }
                        }

                        is SettingProfileUiEvent.ShowMessage -> {
                            showMessage(event.message, event.extraMessage)
                        }

                        is SettingProfileUiEvent.NavigateToLoginActivity -> {
                            navigateToLoginActivity()
                        }
                    }
                }
            }
        }
    }

    private fun setTopAppBar(isFirstSignIn: Boolean) {
        binding.topAppBar.setNavigationOnClickListener {
            if (isFirstSignIn) {
                findNavController().navigate(SettingProfileFragmentDirections.actionSettingProfileFragmentToHomeActivity())
                requireActivity().finish()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun setPhotoPicker() {
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.updateUserProfileImage(uri)
                }
            }

        binding.settingProfileCvProfile.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    override fun onDetach() {
        super.onDetach()
        if (args.isFirstSignIn) {
            callback.remove()
        }
    }
}