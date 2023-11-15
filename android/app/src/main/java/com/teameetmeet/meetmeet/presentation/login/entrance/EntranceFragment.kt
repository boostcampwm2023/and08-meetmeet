package com.teameetmeet.meetmeet.presentation.login.entrance

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentEntranceBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EntranceFragment : BaseFragment<FragmentEntranceBinding>(R.layout.fragment_entrance) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun setClickListener() {
        binding.loginIbKakaoLogin.setOnClickListener {

        }

        binding.loginTvAppLogin.setOnClickListener {
            findNavController().navigate(
                EntranceFragmentDirections.actionEntranceFragmentToSelfLoginFragment()
            )
        }

        binding.loginTvAppSignUp.setOnClickListener {

        }
    }
}