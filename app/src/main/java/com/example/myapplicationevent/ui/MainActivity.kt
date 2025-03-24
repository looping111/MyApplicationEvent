package com.example.myapplicationevent.ui


import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.myapplicationevent.R
import com.example.myapplicationevent.databinding.ActivityMainBinding
import com.example.myapplicationevent.ui.setting.SettingPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_upcoming_event,
                R.id.navigation_finished_event,
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
    }
}