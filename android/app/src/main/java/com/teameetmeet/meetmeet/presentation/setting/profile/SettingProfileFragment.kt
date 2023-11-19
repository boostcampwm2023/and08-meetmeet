package com.teameetmeet.meetmeet.presentation.setting.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSettingProfileBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingProfileFragment :
    BaseFragment<FragmentSettingProfileBinding>(R.layout.fragment_setting_profile) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTopAppBar(arguments?.getBoolean("isFirstSignIn") ?: false)
    }

    private fun setTopAppBar(isFirstSignIn: Boolean) {
        binding.topAppBar.setNavigationOnClickListener {
            if(isFirstSignIn) {
                findNavController().navigate(SettingProfileFragmentDirections.actionSettingProfileFragmentToHomeActivity())
                requireActivity().finish()
            } else {
                findNavController().popBackStack()
            }
        }
    }
}