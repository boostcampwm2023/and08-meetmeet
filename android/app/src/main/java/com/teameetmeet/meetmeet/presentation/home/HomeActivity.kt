package com.teameetmeet.meetmeet.presentation.home

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.databinding.ActivityHomeBinding
import com.teameetmeet.meetmeet.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var navController: NavController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            showMessage(R.string.notification_message_permission_denied)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askNotificationPermission()
        val navHost =
            supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment
        navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)
        viewModel.updateFcmToken()
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.notification_title_request_permission))
                    .setMessage(getString(R.string.notification_message_request_permission))
                    .setPositiveButton(getString(R.string.notification_positive_request_permission)) { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    .setNegativeButton(getString(R.string.notification_negative_request_permission)) { _, _ ->
                    }
                    .show()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}