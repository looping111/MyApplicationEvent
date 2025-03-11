package com.example.myapplicationevent.ui

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationevent.remote.response.EventItem
import com.bumptech.glide.Glide
import com.example.myapplicationevent.databinding.ItemEventBinding
import com.example.myapplicationevent.databinding.ItemEventFinishedBinding
import java.util.Date
import java.util.Locale

class EventAdapter(
    private val onItemClick: (EventItem) -> Unit,
    private val useLayoutA: Boolean
) : ListAdapter<EventItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private const val VIEW_TYPE_A = 1
        private const val VIEW_TYPE_B = 2

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<EventItem>() {
            override fun areItemsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: EventItem, newItem: EventItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (useLayoutA) VIEW_TYPE_A else VIEW_TYPE_B
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_A) {
            val binding =
                ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EventViewHolderA(binding)
        } else {
            val binding =
                ItemEventFinishedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            EventViewHolderB(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val event = getItem(position)
        if (holder is EventViewHolderA) {
            holder.bind(event)
        } else if (holder is EventViewHolderB) {
            holder.bind(event)
        }
    }

    inner class EventViewHolderA(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventItem) {
            with(binding) {
                tvEventTitle.text = event.name
                Glide.with(itemView.context)
                    .load(event.mediaCover)
                    .into(ivEventBanner)
                itemView.setOnClickListener {
                    onItemClick(event)
                }
            }
        }
    }

    inner class EventViewHolderB(private val binding: ItemEventFinishedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventItem) {
            with(binding) {
                tvEventTitle.text = event.name
                tvEventSummary.text = event.summary
                tvStartDate.text = event.beginTime?.let { formatDate(it).toString() }
                tvEndDate.text = event.beginTime?.let { formatDate(it).toString() }
                tvCityName.text = event.cityName
                Glide.with(itemView.context)
                    .load(event.mediaCover)
                    .into(ivEventBanner)
                itemView.setOnClickListener {
                    onItemClick(event)
                }
            }
        }
    }

    private fun formatDate(dateString: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

        return try {

            val date: Date? = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
