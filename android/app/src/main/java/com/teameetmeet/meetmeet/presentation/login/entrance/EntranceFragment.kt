package com.teameetmeet.meetmeet.presentation.login.entrance

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEntranceBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment


class EntranceFragment : BaseFragment<FragmentEntranceBinding>(R.layout.fragment_entrance) {

    private val viewModel: EntranceViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
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
}