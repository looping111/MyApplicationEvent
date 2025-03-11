package com.example.myapplicationevent.local.util

import android.content.Context
import androidx.core.content.edit

class PreferenceManager private constructor(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun setLastSyncTime(time: Long) {
        prefs.edit { putLong(LAST_SYNC_TIME, time) }
    }

    fun getLastSyncTime(): Long {
        return prefs.getLong(LAST_SYNC_TIME, 0)
    }

    companion object {
        private const val PREF_NAME = "EventAppPreferences"
        private const val LAST_SYNC_TIME = "last_sync_time"

        @Volatile
        private var INSTANCE: PreferenceManager? = null

        fun getInstance(context: Context): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PreferenceManager(context).also { INSTANCE = it }
            }
        }
    }
}