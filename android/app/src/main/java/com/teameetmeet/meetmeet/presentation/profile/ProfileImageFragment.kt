package com.teameetmeet.meetmeet.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.FragmentProfileImageBinding
import com.teameetmeet.meetmeet.presentation.base.BaseFragment
import com.teameetmeet.meetmeet.presentation.visitcalendar.VisitCalendarActivity

class ProfileImageFragment : BaseFragment<FragmentProfileImageBinding>(R.layout.fragment_profile_image) {

    private val viewModel: ProfileImageViewModel by viewModels()
    private val args: ProfileImageFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTopAppBar()
        setBinding()
        fetchData()
    }

    private fun fetchData() {
        viewModel.fetchImageUrl(args.imageUrl.orEmpty())
    }

    private fun setBinding() {
        binding.vm = viewModel
    }

    private fun setTopAppBar() {
        binding.profileImageMtb.setNavigationOnClickListener {
            (requireActivity() as? VisitCalendarActivity)?.changeProfileStatus(true)
            findNavController().popBackStack()
        }
    }

}