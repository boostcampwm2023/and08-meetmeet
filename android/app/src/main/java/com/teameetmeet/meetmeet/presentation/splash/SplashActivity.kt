package com.teameetmeet.meetmeet.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivitySplashBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import com.teameetmeet.meetmeet.presentation.home.HomeActivity
import com.teameetmeet.meetmeet.presentation.login.LoginActivity
import com.teameetmeet.meetmeet.service.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationHelper.createNotificationChannels()
        collectViewModelEvent()
    }

    private fun collectViewModelEvent() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect { event ->
                    when (event) {
                        is SplashEvent.NavigateToHomeActivity -> navigateToHomeActivity()
                        is SplashEvent.NavigateToLoginActivity -> navigateToLoginActivity()
                    }
                }
            }
        }
    }

    private fun navigateToHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        })
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}