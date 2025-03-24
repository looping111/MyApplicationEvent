package com.example.myapplicationevent.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.myapplicationevent.R
import com.example.myapplicationevent.remote.response.EventItem
import com.example.myapplicationevent.remote.response.EventResponse
import com.example.myapplicationevent.remote.retrofit.ApiClient
import com.example.myapplicationevent.ui.MainActivity
import retrofit2.Call
import retrofit2.Response

class EventNotificationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "event_notification_channel"
        private const val NOTIFICATION_ID = 100
    }

    override fun doWork(): Result {
        return try {
            val response: Response<EventResponse> = ApiClient.apiService.getUpcomingEvents().execute()
            if (response.isSuccessful && response.body() != null) {
                val eventList: List<EventItem> = response.body()!!.listEvents
                if (eventList.isNotEmpty()) {
                    sendNotification(eventList[0])
                }
            }
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun sendNotification(event: EventItem) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Event Notifications", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("Event Baru: ${event.name}")
            .setContentText(event.summary ?: "")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}