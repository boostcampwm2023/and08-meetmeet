package com.teameetmeet.meetmeet.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding>(private val layoutResId: Int) : Fragment() {

    private var _binding: T? = null
    protected val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun showMessage(messageId: Int, extraMessage: String) {
        if (extraMessage.isNotEmpty()) {
            Toast.makeText(
                requireContext(),
                String.format(getString(messageId), extraMessage),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(requireContext(), getString(messageId), Toast.LENGTH_SHORT).show()
        }
    }
}