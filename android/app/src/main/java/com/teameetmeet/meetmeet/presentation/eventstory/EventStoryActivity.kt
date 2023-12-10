package com.teameetmeet.meetmeet.presentation.eventstory

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityEventStoryBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EventStoryActivity : BaseActivity<ActivityEventStoryBinding>(R.layout.activity_event_story) {

    private val args: EventStoryActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deliverEventIdToCurrentFragment()
    }

    private fun deliverEventIdToCurrentFragment() {
        val bundle = bundleOf(EVENT_ID to args.eventId)
        (binding.eventStoryFcv.getFragment<NavHostFragment>()).navController.setGraph(
            R.navigation.nav_graph_event_story, bundle
        )
    }

    companion object {
        const val EVENT_ID = "eventId"
    }
}