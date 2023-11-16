package com.teameetmeet.meetmeet.presentation.login.signup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSignUpBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignUpBinding>(R.layout.fragment_sign_up) {

    private val viewModel: SignUpViewModel by viewModels()

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
            viewModel.event.collectLatest { event ->
                when (event) {
                    SignUpEvent.SignUpSuccess -> {
                        findNavController().navigate(
                            SignUpFragmentDirections.actionSignUpFragmentToHomeActivity()
                        )
                        requireActivity().finish()
                    }
                }
            }
        }
    }
}