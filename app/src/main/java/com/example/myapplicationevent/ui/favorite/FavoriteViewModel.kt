package com.example.myapplicationevent.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.myapplicationevent.EventRepository
import com.example.myapplicationevent.local.entity.Event

class FavoriteViewModel(private val repository: EventRepository) : ViewModel() {
    fun getFavoriteEvents(): LiveData<List<Event>> = repository.getFavoriteEvents()
}
