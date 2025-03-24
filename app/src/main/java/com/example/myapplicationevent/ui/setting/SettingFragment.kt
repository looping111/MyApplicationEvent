package com.example.myapplicationevent.ui.setting

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.myapplicationevent.R
import com.example.myapplicationevent.worker.EventNotificationWorker
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private lateinit var settingPreferences: SettingPreferences
    private lateinit var switchDarkMode: SwitchMaterial
    private lateinit var switchNotification: SwitchMaterial

    private val workTag = "event_notification_work"
    private val channelId = "event_notification_channel"

    // Register the permissions callback
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
                // Start the worker if permission is granted and the switch is on
                if (switchNotification.isChecked) {
                    startEventNotificationWorker()
                }
            } else {
                Toast.makeText(requireContext(), "Notification permission denied", Toast.LENGTH_SHORT).show()
                // Turn off the switch if permission is denied
                switchNotification.isChecked = false
                lifecycleScope.launch {
                    settingPreferences.saveNotificationEnabled(false)
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        switchDarkMode = view.findViewById(R.id.switch_theme)
        switchNotification = view.findViewById(R.id.switch_notification)

        // Create notification channel
        createNotificationChannel(requireContext())

        settingPreferences = SettingPreferences(requireContext())

        lifecycleScope.launch {
            settingPreferences.darkModeFlow.collect { isDarkMode ->
                switchDarkMode.isChecked = isDarkMode
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingPreferences.saveDarkMode(isChecked)
            }
        }

        lifecycleScope.launch {
            settingPreferences.notificationEnabledFlow.collect { isEnabled ->
                switchNotification.isChecked = isEnabled
                if (isEnabled) {
                    // Check for permission before starting the worker
                    if (isNotificationPermissionGranted(requireContext())) {
                        startEventNotificationWorker()
                    } else {
                        requestNotificationPermission()
                    }
                } else {
                    stopEventNotificationWorker()
                }
            }
        }

        switchNotification.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                settingPreferences.saveNotificationEnabled(isChecked)
                if (isChecked) {
                    // Check for permission before starting the worker
                    if (isNotificationPermissionGranted(requireContext())) {
                        startEventNotificationWorker()
                    } else {
                        requestNotificationPermission()
                    }
                } else {
                    stopEventNotificationWorker()
                }
            }
        }
    }

    private fun startEventNotificationWorker() {
        // Periksa apakah aplikasi terdaftar di pengaturan notifikasi
        if (!isAppNotificationsEnabled(requireContext())) {
            Toast.makeText(requireContext(), "Please enable app notifications in settings", Toast.LENGTH_SHORT).show()
            openAppNotificationSettings(requireContext())
            return
        }
        val workRequest = PeriodicWorkRequest.Builder(EventNotificationWorker::class.java, 1, TimeUnit.HOURS)
            .addTag(workTag)
            .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(workTag, ExistingPeriodicWorkPolicy.REPLACE, workRequest)
    }

    private fun stopEventNotificationWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork(workTag)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Event Notifications"
            val descriptionText = "Notifications for events"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // No permission needed for older versions
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(requireContext(), "Notification permission is needed to show notifications", Toast.LENGTH_SHORT).show()
            }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun isAppNotificationsEnabled(context: Context): Boolean {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.getNotificationChannel(channelId)?.let {
                notificationManager.areNotificationsEnabled() && it.importance != NotificationManager.IMPORTANCE_NONE
            } ?: false
        } else {
            notificationManager.areNotificationsEnabled()
        }
    }

    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }
}