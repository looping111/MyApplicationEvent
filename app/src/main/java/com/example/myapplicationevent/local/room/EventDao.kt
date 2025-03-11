package com.example.myapplicationevent.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myapplicationevent.local.entity.Event

@Dao
interface EventDao {
    @Query("SELECT * FROM events WHERE is_finished = 0")
    fun getActiveEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE is_finished = 1")
    fun getFinishedEvents(): LiveData<List<Event>>

    @Query("SELECT event_id FROM events WHERE is_favorite = 1")
    suspend fun getFavoriteEventIds(): List<Long>

    @Query("DELETE FROM events WHERE is_favorite = 0")
    suspend fun deleteNonFavoriteEvents()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateEvent(event: Event)

    @Query("SELECT COUNT(*) FROM events")
    suspend fun getEventCount(): Int

    @Query("SELECT * FROM events WHERE is_favorite = 1")
    fun getFavoriteEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): Event?

    @Query("SELECT is_favorite FROM events WHERE id = :eventId")
    suspend fun isEventFavorite(eventId: String): Boolean

    @Query("UPDATE events SET is_favorite = :isFavorite WHERE event_id = :eventId")
    suspend fun updateFavoriteStatus(eventId: String, isFavorite: Boolean)
}