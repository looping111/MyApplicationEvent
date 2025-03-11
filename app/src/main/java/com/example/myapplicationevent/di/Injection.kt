package com.example.myapplicationevent.di

import android.content.Context
import com.example.myapplicationevent.EventRepository
import com.example.myapplicationevent.local.room.EventDatabase
import com.example.myapplicationevent.local.util.PreferenceManager
import com.example.myapplicationevent.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): EventRepository {
        val apiService = ApiConfig.getApiService()
        val database = EventDatabase.getDatabase(context)
        val dao = database.eventDao()
        val preferenceManager = PreferenceManager.getInstance(context)
        return EventRepository.getInstance(dao, apiService, preferenceManager)
    }
}