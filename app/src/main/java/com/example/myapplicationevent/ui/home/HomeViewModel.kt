package com.example.myapplicationevent.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationevent.EventRepository
import com.example.myapplicationevent.local.entity.Event
import com.example.myapplicationevent.local.util.EventWrapper
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: EventRepository) : ViewModel() {
    private val _snackbarText = MutableLiveData<EventWrapper<String>>()
    val snackbarText: LiveData<EventWrapper<String>> = _snackbarText

    // LiveData from the repository for active and finished events
    val activeEvents: LiveData<List<Event>> = repository.getActiveEvents()
    val finishedEvents: LiveData<List<Event>> = repository.getFinishedEvents()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        refreshEventsIfNeeded()
    }

    // Refresh events from the repository
    fun refreshEvents() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.refreshEvents()
            } catch (e: Exception) {

                _snackbarText.value = EventWrapper("Failed to refresh events: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    private fun refreshEventsIfNeeded() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Check if we should fetch data from the network
                if (repository.shouldFetchFromNetwork()) {
                    repository.refreshEvents()
                }
            } catch (e: Exception) {
                _snackbarText.value = EventWrapper("Failed to refresh events: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}