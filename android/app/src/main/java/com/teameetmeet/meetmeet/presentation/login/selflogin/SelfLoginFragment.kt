package com.teameetmeet.meetmeet.presentation.login.selflogin

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentSelfLoginBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelfLoginFragment : BaseFragment<FragmentSelfLoginBinding>(R.layout.fragment_self_login) {

    private val viewModel: SelfLoginViewModel by viewModels()

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
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collectLatest { event ->
                    when (event) {
                        is SelfLoginUiEvent.NavigateToHomeActivity -> {
                            findNavController().navigate(
                                SelfLoginFragmentDirections.actionSelfLoginFragmentToHomeActivity()
                            )
                            requireActivity().finishAffinity()
                        }

                        is SelfLoginUiEvent.ShowMessage -> {
                            showMessage(event.message, event.extraMessage)
                        }
                    }
                }
            }
        }
    }
}