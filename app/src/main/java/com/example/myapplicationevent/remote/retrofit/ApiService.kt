package com.example.myapplicationevent.remote.retrofit

import com.example.myapplicationevent.remote.response.EventDetailResponse
import com.example.myapplicationevent.remote.response.EventResponse
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events")
    fun getActiveEvents(
        @Query("active") active: Int? = 1
    ): Call<EventResponse>

    @GET("events")
    fun getFinishedEvents(
        @Query("active") active: Int? = 0
    ): Call<EventResponse>

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int? = -1,
        @Query("q") searchQuery: String
    ): Call<EventResponse>

    @GET("events/{id}")
    fun getDetailEvent(
        @Path("id") id: String,
    ): Call<EventDetailResponse>

    @GET("events?active=1")
    fun getUpcomingEvents(): Call<EventResponse>
}

object ApiClient {
    private const val BASE_URL = "https://event-api.dicoding.dev/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}