package com.teameetmeet.meetmeet.presentation.login.entrance

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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

        }

        binding.loginTvAppSignUp.setOnClickListener {

        }
    }

    private fun collectViewModelEvent() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.kakaoLoginEvent.collect {
                    when(it) {
                        is KakaoLoginEvent.Success -> {
                            navigateToHomeActivity(it.id)
                        }
                        is KakaoLoginEvent.Failure -> {
                            showMessage(it.message)
                        }
                    }
                }
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHomeActivity(id: Long) {
        findNavController().navigate(EntranceFragmentDirections.entranceFragmentToHomeActivity(id))
    }
}