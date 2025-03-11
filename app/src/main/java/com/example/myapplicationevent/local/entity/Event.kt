package com.example.myapplicationevent.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myapplicationevent.remote.response.EventItem
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id")
    val eventId: Long = 0,

    @Embedded
    @field:SerializedName("event")
    val eventItem: EventItem,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "is_finished")
    val isFinished:Boolean = false

):Parcelable