package com.teameetmeet.meetmeet.presentation.visitcalendar

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityVisitCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.MonthCalendarFragment
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.MonthCalendarFragmentDirections
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm.OthersMonthCalendarViewModel
import com.teameetmeet.meetmeet.presentation.util.setClickEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VisitCalendarActivity : BaseActivity<ActivityVisitCalendarBinding>(
    R.layout.activity_visit_calendar
) {
    private val args: VisitCalendarActivityArgs by navArgs()
    private val viewModel: VisitCalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNavHost()
        setTopAppBar()
        binding.vm = viewModel
        binding.followBtnAction.setClickEvent(lifecycleScope) {
            viewModel.onProfileImageClick()
        }
        viewModel.fetchUserProfile(args.userNickname)
        collectViewModelEvent()
    }

    private fun collectViewModelEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is VisitCalendarEvent.NavigateToProfileImageFragment -> {
                            val navHostFragment =
                                binding.fragmentContainer.getFragment<NavHostFragment>()
                            val currentFragment =
                                navHostFragment.childFragmentManager.primaryNavigationFragment
                                    ?: return@collect
                            if (currentFragment is MonthCalendarFragment) {
                                changeProfileStatus(false)
                                navHostFragment.navController.navigate(
                                    MonthCalendarFragmentDirections.actionMonthCalendarFragmentToProfileImageFragment(
                                        event.imageUrl
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setTopAppBar() {
        binding.visitCalendarMtb.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setNavHost() {
        val bundle = bundleOf(
            MonthCalendarFragment.TYPE_KEY to OthersMonthCalendarViewModel.TYPE,
            MonthCalendarFragment.USER_ID to args.userId
        )
        (binding.fragmentContainer.getFragment<NavHostFragment>()).navController.setGraph(
            R.navigation.nav_graph_calendar, bundle
        )
    }

    fun changeProfileStatus(status: Boolean) {
        viewModel.changeProfileStatus(status)
    }
}