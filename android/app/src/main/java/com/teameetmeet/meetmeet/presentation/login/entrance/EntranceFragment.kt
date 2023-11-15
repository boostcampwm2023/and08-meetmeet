package com.teameetmeet.meetmeet.presentation.login.entrance

import android.os.Bundle
import android.view.View
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEntranceBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment


class EntranceFragment : BaseFragment<FragmentEntranceBinding>(R.layout.fragment_entrance) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.loginIbKakaoLogin.setOnClickListener {

        }

        binding.loginTvAppLogin.setOnClickListener {

        }

        binding.loginTvAppSignUp.setOnClickListener {

        }
    }
}