package com.teameetmeet.meetmeet.presentation.login.entrance

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEntranceBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntranceFragment : BaseFragment<FragmentEntranceBinding>(R.layout.fragment_entrance) {

    private val viewModel: EntranceViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
        collectViewModelEvent()
    }

    private fun setClickListener() {
        binding.loginIbKakaoLogin.setOnClickListener {
            viewModel.loginKakao()
        }

        binding.loginTvAppLogin.setOnClickListener {
            findNavController().navigate(
                EntranceFragmentDirections.actionEntranceFragmentToSelfLoginFragment()
            )
        }

        binding.loginTvAppSignUp.setOnClickListener {
            findNavController().navigate(
                EntranceFragmentDirections.actionEntranceFragmentToSignUpFragment()
            )
        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.kakaoLoginEvent.collect {
                    when (it) {
                        is KakaoLoginEvent.NavigateToHomeActivity -> {
                            navigateToHomeActivity()
                        }

                        is KakaoLoginEvent.ShowMessage -> {
                            showMessage(it.message, it.extraMessage)
                        }

                        is KakaoLoginEvent.NavigateToProfileSettingFragment -> {
                            navigateToProfileSettingFragment()
                        }
                    }
                }
            }
        }
    }


    private fun navigateToHomeActivity() {
        findNavController().navigate(EntranceFragmentDirections.actionEntranceFragmentToHomeActivity())
    }

    private fun navigateToProfileSettingFragment() {
        findNavController().navigate(
            EntranceFragmentDirections.actionEntranceFragmentToSettingProfileFragment()
                .setIsFirstSignIn(true)
        )
    }
}