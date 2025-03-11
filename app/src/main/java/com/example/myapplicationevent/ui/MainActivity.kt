package com.example.myapplicationevent.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myapplicationevent.R
import com.example.myapplicationevent.databinding.ActivityMainBinding
import com.example.myapplicationevent.ui.setting.SettingPreferences
//import com.example.myapplicationevent.ui.setting.SettingFragment
//import com.example.myapplicationevent.ui.setting.SettingFragment.Companion.NOTIFICATION_PREFERENCES_KEY
//import com.example.myapplicationevent.ui.setting.SettingFragment.Companion.REQUEST_CODE_NOTIFICATION_PERMISSION
//import com.example.myapplicationevent.worker.DailyRemainderWorker
//import com.example.myapplicationevent.worker.SyncWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingPreferences = SettingPreferences(applicationContext)
        lifecycleScope.launch {
            val isDarkMode = settingPreferences.darkModeFlow.first()
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

//        applyThemFromPreference()

//        if (Build.VERSION.SDK_INT >= 33) {
//            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
//        }
        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_finished_event,
                R.id.navigation_upcoming_event,
                R.id.navigation_detail_event,
                R.id.navigation_favorite_event,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Get the eventId from the Intent
        val eventId = intent.getStringExtra("eventId")

        // Pass the eventId to the DetailEventFragment
        if (savedInstanceState == null) {
            if (!eventId.isNullOrEmpty()) {
                val bundle = Bundle().apply {
                    putString("eventId", eventId)
                }
                navController.navigate(R.id.navigation_detail_event, bundle)
            }
        }
//        setupSyncDataWork()
//        createNotificationChannel()
//        setupNotificationPreference(PreferenceManager.getDefaultSharedPreferences(this))
    }



//    private fun createNotificationChannel() {
//        val name = "Daily Reminder"
//        val descriptionText = "Channel for daily event reminders"
//        val importance = NotificationManager.IMPORTANCE_HIGH
//        val channel = NotificationChannel(DailyRemainderWorker.CHANNEL_ID, name, importance).apply {
//            description = descriptionText
//        }
//
//        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }
//
//    private fun applyThemFromPreference() {
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        val isDarkMode = sharedPreferences.getBoolean(SettingFragment.THEME_KEY, false)
//
//        if (isDarkMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//
//        }
//    }
//
//    private fun scheduleDailyReminder() {
//        val dailyReminderWork =
//            PeriodicWorkRequestBuilder<DailyRemainderWorker>(1, TimeUnit.DAYS)
//                .build()
//        WorkManager.getInstance(this).enqueue(dailyReminderWork)
//    }
//
//    private fun cancelDailyReminder() {
//        WorkManager.getInstance(this).cancelAllWorkByTag("DailyReminder")
//    }
//
//    private fun setupNotificationPreference(sharedPreferences: SharedPreferences) {
//        val isNotificationEnabled =
//            sharedPreferences.getBoolean(NOTIFICATION_PREFERENCES_KEY, false)
//
//        if (isNotificationEnabled) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                // Check for notification permission
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//                    // Request permission
//                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_NOTIFICATION_PERMISSION)
//                } else {
//                    // Schedule daily reminder if permission is granted
//                    scheduleDailyReminder()
//                }
//            } else {
//                // Schedule daily reminder for older versions
//                scheduleDailyReminder()
//            }
//        } else {
//            cancelDailyReminder()
//        }
//    }
//
//    private fun setupSyncDataWork() {
//        SyncWorker.setupPeriodicSync(applicationContext)
//    }
}