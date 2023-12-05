package com.teameetmeet.meetmeet.presentation.visitcalendar

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityVisitCalendarBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.MonthCalendarFragment
import com.teameetmeet.meetmeet.presentation.calendar.monthcalendar.vm.OthersMonthCalendarViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VisitCalendarActivity : BaseActivity<ActivityVisitCalendarBinding>(
    R.layout.activity_visit_calendar
) {
    private val args: VisitCalendarActivityArgs by navArgs()
    private val viewModel: VisitCalendarViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNavHost()
        viewModel.fetchUserProfile(args.userNickname)
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
}