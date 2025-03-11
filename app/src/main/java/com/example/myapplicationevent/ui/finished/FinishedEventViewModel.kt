package com.example.myapplicationevent.ui.finished

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationevent.EventRepository
import com.example.myapplicationevent.remote.response.EventItem
import com.example.myapplicationevent.remote.response.EventResponse
import com.example.myapplicationevent.Result
import com.example.myapplicationevent.local.util.EventWrapper
import kotlinx.coroutines.launch

class FinishedEventViewModel(private val repository: EventRepository) : ViewModel() {
    private val _searchedEvents = MutableLiveData<List<EventItem>>()
    val searchedEvents: LiveData<List<EventItem>> = _searchedEvents

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<EventWrapper<String>>()
    val snackbarText: LiveData<EventWrapper<String>> = _snackbarText


    fun searchEvents(searchQuery: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result: Result<EventResponse> =
                repository.searchEvents(active = -1, searchQuery)
            _isLoading.value = false

            when (result) {
                is Result.Success -> {
                    _searchedEvents.value = result.data.listEvents
                }

                is Result.Error -> {
                    Log.e(TAG, "Error: ${result.error}")
                    _snackbarText.value = EventWrapper(result.error)
                }
            }
        }
    }

    companion object {
        private const val TAG = "FinishedEventViewModel"
    }
}