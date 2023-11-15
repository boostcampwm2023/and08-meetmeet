package com.teameetmeet.meetmeet.presentation.searchevent

import android.os.Bundle
import androidx.activity.viewModels
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivitySearchEventBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity

class SearchActivity : BaseActivity<ActivitySearchEventBinding>(
    R.layout.activity_search_event
) {
    private val viewModel: SearchEventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(binding) {
            vm = viewModel
            lifecycleOwner = this@SearchActivity
            searchEventSv.setupWithSearchBar(searchEventSb)
            searchEventSpinner.setSelection(1)
        }
    }
}