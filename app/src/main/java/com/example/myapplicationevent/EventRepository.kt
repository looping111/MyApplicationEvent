package com.example.myapplicationevent

import androidx.lifecycle.LiveData
import com.example.myapplicationevent.local.entity.Event
import com.example.myapplicationevent.local.room.EventDao
import com.example.myapplicationevent.local.util.PreferenceManager
import com.example.myapplicationevent.remote.response.EventResponse
import com.example.myapplicationevent.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.awaitResponse
import java.io.IOException
import java.util.concurrent.TimeUnit

class EventRepository private constructor(
    private val eventDao: EventDao,
    private val apiService: ApiService,
    private val prefManager: PreferenceManager
) {
    fun getActiveEvents(): LiveData<List<Event>> = eventDao.getActiveEvents()
    fun getFinishedEvents(): LiveData<List<Event>> = eventDao.getFinishedEvents()

    suspend fun refreshEvents() {
        withContext(Dispatchers.IO) {
            try {
                val activeEvents =
                    apiService.getActiveEvents().execute().body()?.listEvents ?: emptyList()
                val finishedEvents =
                    apiService.getFinishedEvents().execute().body()?.listEvents ?: emptyList()

                // Get current favorite events
                val favoriteEventIds = eventDao.getFavoriteEventIds()

                // Clear all events except favorites
                eventDao.deleteNonFavoriteEvents()

                // Insert or update events
                val allEvents = activeEvents.map { Event(eventItem = it, isFinished = false) } +
                        finishedEvents.map { Event(eventItem = it, isFinished = true) }

                allEvents.forEach { event ->
                    val isFavorite = favoriteEventIds.contains(event.eventId)
                    eventDao.insertOrUpdateEvent(event.copy(isFavorite = isFavorite))
                }

                // Update last sync time
                prefManager.setLastSyncTime(System.currentTimeMillis())
            } catch (e: IOException) {
                // Handle network errors (e.g., no internet connection)
                Result.Error("Network error: Unable to fetch events. Please check your connection.")
                throw e
            } catch (e: HttpException) {
                // Handle API errors (e.g., server is down, 404, etc.)
                Result.Error("Server error: Unable to fetch events. Please try again later.")
                throw e
            } catch (e: Exception) {
                // Handle any other generic errors
                Result.Error("Unexpected error: ${e.message}")
                throw e
            }
        }
    }

    suspend fun toggleFavorite(eventId: String) {
        withContext(Dispatchers.IO) {
            val event = eventDao.getEventById(eventId)
            event?.let {
                val updatedEvent = it.copy(isFavorite = !it.isFavorite)
                eventDao.insertOrUpdateEvent(updatedEvent)
            }
        }
    }


    fun getFavoriteEvents(): LiveData<List<Event>> = eventDao.getFavoriteEvents()

    suspend fun isEventFavorite(eventId: String): Boolean {
        return withContext(Dispatchers.IO) {
            eventDao.isEventFavorite(eventId)
        }
    }

    suspend fun searchEvents(active: Int, searchQuery: String): Result<EventResponse> {
        return try {
            val response = apiService.searchEvents(active, searchQuery).awaitResponse()
            if (response.isSuccessful) {
                Result.Success(response.body()!!)
            } else {
                Result.Error("Failed to search events: ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Error searching events: ${e.message ?: "Unknown error"}")
        }
    }

    suspend fun shouldFetchFromNetwork(): Boolean {
        val lastSyncTime = prefManager.getLastSyncTime()
        val currentTime = System.currentTimeMillis()
        val timeSinceLastSync = currentTime - lastSyncTime

        return timeSinceLastSync > TimeUnit.HOURS.toMillis(6) || eventDao.getEventCount() == 0
    }

    companion object {
        @Volatile
        private var instance: EventRepository? = null

        fun getInstance(
            eventDao: EventDao,
            apiService: ApiService,
            prefManager: PreferenceManager
        ): EventRepository =
            instance ?: synchronized(this) {
                instance ?: EventRepository(eventDao, apiService, prefManager).also {
                    instance = it
                }
            }
    }
}