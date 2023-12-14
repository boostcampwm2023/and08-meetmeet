package com.teameetmeet.meetmeet.presentation.login.entrance

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEntranceBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
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
        binding.loginIbKakaoLogin.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            loginKakao()
        }

        binding.loginTvAppLogin.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            findNavController().navigate(
                EntranceFragmentDirections.actionEntranceFragmentToSelfLoginFragment()
            )
        }

        binding.loginTvAppSignUp.setClickEvent(viewLifecycleOwner.lifecycleScope) {
            findNavController().navigate(
                EntranceFragmentDirections.actionEntranceFragmentToSignUpFragment()
            )
        }
    }

    private val kakaoLoginCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            showMessage(R.string.login_kakao_message_kakao_login_fail, error.message.orEmpty())
        } else if (token != null) {
            viewModel.loginApp()
        }
    }

    private fun loginKakao() {
        lifecycleScope.launch {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
                UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                    if (error != null) {
                       showMessage(R.string.login_kakao_message_kakao_login_fail, error.message.orEmpty())
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        UserApiClient.instance.loginWithKakaoAccount(
                            requireContext(),
                            callback = kakaoLoginCallback
                        )
                    } else if (token != null) {
                        viewModel.loginApp()
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    requireContext(),
                    callback = kakaoLoginCallback
                )
            }
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
        requireActivity().finishAffinity()
    }

    private fun navigateToProfileSettingFragment() {
        findNavController().navigate(
            EntranceFragmentDirections.actionEntranceFragmentToSettingProfileFragment()
                .setIsFirstSignIn(true)
        )
    }
}