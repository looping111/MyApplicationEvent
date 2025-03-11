package com.example.myapplicationevent.ui.setting

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("AppPrefs", 0)

    private val _isDarkMode = MutableLiveData<Boolean>().apply {
        value = sharedPreferences.getBoolean("DARK_MODE", false)
    }
    val isDarkMode: LiveData<Boolean> = _isDarkMode

    fun setDarkMode(isEnabled: Boolean) {
        _isDarkMode.value = isEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (isEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
        sharedPreferences.edit().putBoolean("DARK_MODE", isEnabled).apply()
    }
}
