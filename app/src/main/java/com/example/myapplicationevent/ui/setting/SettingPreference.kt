package com.example.myapplicationevent.ui.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Inisialisasi DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingPreferences(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("DARK_MODE")
        private val NOTIFICATION_KEY = booleanPreferencesKey("NOTIFICATION_ENABLED") // Tambahan untuk notifikasi
    }

    // Flow untuk membaca status Dark Mode
    val darkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_KEY] ?: false
        }

    // Fungsi untuk menyimpan status Dark Mode
    suspend fun saveDarkMode(isDarkMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDarkMode
        }
    }

    // Flow untuk membaca status Notifikasi
    val notificationEnabledFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATION_KEY] ?: false
        }

    // Fungsi untuk menyimpan status Notifikasi
    suspend fun saveNotificationEnabled(isEnabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_KEY] = isEnabled
        }
    }
}
