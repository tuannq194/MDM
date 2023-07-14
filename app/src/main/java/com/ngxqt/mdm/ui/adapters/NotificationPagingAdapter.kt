package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.mdm.data.model.objectmodel.Notification
import com.ngxqt.mdm.databinding.ItemNotificationBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationPagingAdapter() :
    PagingDataAdapter<Notification, NotificationPagingAdapter.NotificationViewHolder>(EVENT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class NotificationViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.apply {
                notiDate.text = notification.createdAt.let {
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                    val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                    val date = inputFormat.parse(it.toString())
                    "Thời gian: ${outputFormat.format(date)}"
                }
                notiContent.text = "Nội dung: ${notification.content}"
                notiUser.text = "Người gửi: ${notification.notificationUser?.name}"
            }
        }
    }

    companion object{
        private val EVENT_COMPARATOR = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }
        }
    }
}