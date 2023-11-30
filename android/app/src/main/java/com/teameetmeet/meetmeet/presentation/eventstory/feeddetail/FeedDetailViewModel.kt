package com.teameetmeet.meetmeet.presentation.eventstory.feeddetail

import androidx.lifecycle.ViewModel
import com.teameetmeet.meetmeet.data.repository.EventStoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeedDetailViewModel @Inject constructor(
    private val eventStoryRepository: EventStoryRepository
) : ViewModel() {
    
}