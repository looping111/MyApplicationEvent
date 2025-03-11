package com.example.myapplicationevent.ui.detailevent

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationevent.EventRepository
import com.example.myapplicationevent.remote.response.EventDetailResponse
import com.example.myapplicationevent.remote.response.EventItem
import com.example.myapplicationevent.remote.retrofit.ApiConfig
import com.example.myapplicationevent.local.util.EventWrapper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventViewModel(private val repository: EventRepository) : ViewModel() {
    private val _detailEvent = MutableLiveData<EventItem>()
    val detailEvent: LiveData<EventItem> = _detailEvent

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<EventWrapper<String>>()
    val snackbarText: LiveData<EventWrapper<String>> = _snackbarText

    companion object{
        const val TAG = "detailEventViewModel"
    }

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun toggleFavorite(eventId: String) {
        Log.d(TAG, eventId)
        viewModelScope.launch {
            repository.toggleFavorite(eventId)
            _isFavorite.value = repository.isEventFavorite(eventId)
            Log.d(TAG, _isFavorite.value.toString())
        }
    }

    fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.isEventFavorite(eventId)
            Log.d(TAG, _isFavorite.value.toString())
        }
    }

    fun fetchDetail(eventId: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getDetailEvent(eventId)
        client.enqueue(object : Callback<EventDetailResponse> {
            override fun onResponse(call: Call<EventDetailResponse>, response: Response<EventDetailResponse>) {
                _isLoading.value = false
                if (response.isSuccessful && response.body() != null) {
                    _detailEvent.value = response.body()?.event
                }else{
                    Log.e(TAG, "onFailure: ${response.message()}")
                    if (response.body()?.message?.trim() == "Unable to resolve host \"event-api.dicoding.dev\": No address associated with hostname") {
                        _snackbarText.value = EventWrapper("No Internet Connection")
                    } else {
                        _snackbarText.value = EventWrapper(response.body()?.message.toString())
                    }
                }
            }

            override fun onFailure(call: Call<EventDetailResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure: ${t.message.toString()}")
                if (t.message?.trim() == "Unable to resolve host \"event-api.dicoding.dev\": No address associated with hostname") {
                    _snackbarText.value = EventWrapper("No Internet Connection")
                } else {
                    _snackbarText.value = EventWrapper(t.message.toString())
                }
            }

        })
    }


}