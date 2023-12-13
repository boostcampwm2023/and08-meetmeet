package com.teameetmeet.meetmeet.presentation.notification

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayoutMediator
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityNotificationBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import com.teameetmeet.meetmeet.presentation.home.HomeActivity
import com.teameetmeet.meetmeet.presentation.util.setMenuClickEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationActivity :
    BaseActivity<ActivityNotificationBinding>(R.layout.activity_notification) {

    private lateinit var notificationAdapter: NotificationAdapter

    private val eventNotificationViewModel: EventNotificationViewModel by viewModels()
    private val followNotificationViewModel: FollowNotificationViewModel by viewModels()
    private val groupNotificationViewModel: GroupNotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBackPressedCallback()
        setTabLayout()
        setTopAppBar()
    }

    private fun setBackPressedCallback() {
        onBackPressedDispatcher.addCallback(
            this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateToPrev()
                }
            }
        )
    }

    private fun setTopAppBar() {
        binding.notificationMtb.setNavigationOnClickListener {
            navigateToPrev()
        }
        binding.notificationMtb.setMenuClickEvent(lifecycleScope) { menuItemId ->
            when (menuItemId) {
                R.id.menu_delete_notification_all -> {
                    when (binding.notificationVp.currentItem) {
                        TAB_INDEX_FOLLOW -> {
                            followNotificationViewModel.onDeleteAll()
                        }

                        TAB_INDEX_EVENT_INVITATION -> {
                            eventNotificationViewModel.onDeleteAll()
                        }

                        TAB_INDEX_GROUP_INVITATION -> {
                        }
                    }
                }
            }
        }
    }

    private fun navigateToPrev() {
        if (!isTaskRoot) finish()
        else navigateToHomeActivity()
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
        finish()
    }

    private fun setTabLayout() {
        notificationAdapter = NotificationAdapter(this)
        binding.notificationVp.adapter = notificationAdapter
        TabLayoutMediator(binding.notificationTl, binding.notificationVp) { tab, position ->
            when (position) {
                TAB_INDEX_FOLLOW -> tab.text = getString(R.string.notification_tab_follow)
                TAB_INDEX_EVENT_INVITATION -> tab.text =
                    getString(R.string.notification_tab_invite_event)

                TAB_INDEX_GROUP_INVITATION -> tab.text =
                    getString(R.string.notification_tab_invite_group)
            }
        }.attach()
        binding.notificationVp.currentItem = intent.getIntExtra(TAB_INDEX, TAB_INDEX_FOLLOW)
    }

    companion object {
        const val TAB_INDEX = "tabIndex"
        const val TAB_INDEX_FOLLOW = 0
        const val TAB_INDEX_EVENT_INVITATION = 1
        const val TAB_INDEX_GROUP_INVITATION = 2
    }
}