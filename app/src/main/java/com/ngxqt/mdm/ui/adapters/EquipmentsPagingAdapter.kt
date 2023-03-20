package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.databinding.ItemEquipmentBinding
import com.ngxqt.mdm.util.BASE_URL_KA

class EquipmentsPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Equipment, EquipmentsPagingAdapter.EquipmentViewHolder>(EQUIPMENT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val binding = ItemEquipmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EquipmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class EquipmentViewHolder(private val binding: ItemEquipmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val equipment = getItem(position)
                    if (equipment!=null)
                        listener.onItemClick(equipment)
                }
            }
        }

        fun bind(equipment: Equipment) {
            binding.apply {
                Glide.with(itemView)
                    .load(BASE_URL_KA +"/public/uploads/"+equipment.path)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.logo)
                    .into(equipmentImage)
                //equipmentImage.setImageResource(R.drawable.logo)
                equipmentTitle.text = equipment.title
                equipmentModel.text = "Model: ${equipment.model?.trim()}"
                equipmentSerial.text = "Serial: ${equipment.serial?.trim()}"
                equipmentStatus.text = equipment.status?.trim().let {
                    if (it == "active") {"Đang Sử Dụng"}
                    else if (it == "was_broken") {"Đang Báo Hỏng"}
                    else if (it == "corrected") {"Đang Sửa Chữa"}
                    else if (it == "liquidated") {"Đã Thanh Lý"}
                    else if (it == "inactive") {"Ngừng Sử Dụng"}
                    else if (it == "not_handed") {"Mới"}
                    else {""}
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(equipment: Equipment)
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