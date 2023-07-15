package com.ngxqt.mdm.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.databinding.ItemInventoryHistoryBinding
import com.ngxqt.mdm.ui.dialog.MyDialog.Companion.INVENTORY_HISTORY_DIALOG
import com.ngxqt.mdm.ui.dialog.MyDialog.Companion.REPAIR_HISTORY_DIALOG
import java.text.SimpleDateFormat
import java.util.*

class HistoryPagingAdapter(private val typeHistory: String?) :
    PagingDataAdapter<Equipment, HistoryPagingAdapter.EquipmentViewHolder>(EQUIPMENT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val binding = ItemInventoryHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EquipmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class EquipmentViewHolder(private val binding: ItemInventoryHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(equipment: Equipment) {
            binding.apply {
                when (typeHistory) {
                    INVENTORY_HISTORY_DIALOG -> {
                        inventoryCreatedDate.text = equipment.inventoryDate?.let {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                            val date = inputFormat.parse(it)
                            "Ngày kiểm kê: ${outputFormat.format(date as Date)}"
                        } ?: "Ngày kiểm kê: Không có dữ liệu"

                        inventoryNote.text = equipment.note?.let {
                            "Ghi chú: ${it.trim()}"
                        } ?: "Ghi chú: Không có dữ liệu"

                        inventoryCreatedUser.text = equipment.inventoryCreateUser?.name?.let {
                            "Người kiểm kê: ${it.trim()}"
                        } ?: "Người kiểm kê: Không có dữ liệu"
                    }
                    REPAIR_HISTORY_DIALOG -> {
                        inventoryCreatedDate.text = equipment.brokenReportDate?.let {
                            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
                            val outputFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                            val date = inputFormat.parse(it)
                            "Ngày báo hỏng: ${outputFormat.format(date as Date)}"
                        } ?: "Ngày báo hỏng: Không có dữ liệu"

                        inventoryNote.text = equipment.reason?.let {
                            "Lí do sửa chữa: ${it.trim()}"
                        } ?: "Lí do sửa chữa: Không có dữ liệu"

                        inventoryCreatedUser.visibility = View.GONE
                    }
                }
            }
        }
    }

    companion object{
        private val EQUIPMENT_COMPARATOR = object : DiffUtil.ItemCallback<Equipment>() {
            override fun areItemsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Equipment, newItem: Equipment): Boolean {
                return oldItem == newItem
            }
        }
    }
}