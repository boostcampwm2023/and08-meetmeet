package com.teameetmeet.meetmeet.presentation.setting.profile

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingProfileBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onDetach() {
        super.onDetach()
        if (args.isFirstSignIn) {
            callback.remove()
        }
    }
}