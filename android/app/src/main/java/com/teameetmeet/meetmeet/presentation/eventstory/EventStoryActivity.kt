package com.teameetmeet.meetmeet.presentation.eventstory

import android.os.Bundle
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
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.event_story_fcv)
        val eventStoryFragment = navHostFragment?.childFragmentManager?.fragments?.get(0)
        val bundle = Bundle()
        bundle.putInt(EVENT_ID, args.eventId)
        eventStoryFragment?.arguments = bundle
    }

    companion object {
        const val EVENT_ID = "eventId"
    }
}