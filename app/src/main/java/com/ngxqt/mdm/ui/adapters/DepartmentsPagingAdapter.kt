package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.mdm.data.model.Department
import com.ngxqt.mdm.databinding.ItemDepartmentBinding

class DepartmentsPagingAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<Department, DepartmentsPagingAdapter.DepartmentViewHolder>(DEPARTMENT_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepartmentViewHolder {
        val binding = ItemDepartmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DepartmentViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class DepartmentViewHolder(private val binding: ItemDepartmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                departmentEmail.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val department = getItem(position)
                        if (department!=null)
                            listener.onEmailClick(department)
                    }
                }
                departmentPhone.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val department = getItem(position)
                        if (department!=null)
                            listener.onPhoneClick(department)
                    }
                }
                departmentListEquipment.setOnClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val department = getItem(position)
                        if (department!=null)
                            listener.onListEquipClick(department)
                    }
                }
            }
        }

        fun bind(department: Department) {
            binding.apply {
                departmentTitle.text = "${department.name?.trim()?: "Không có dữ liệu"}"
                departmentEmailText.text = "${department.email?.trim()?: "Không có dữ liệu"}"
                departmentPhoneText.text = "${department.phone?.trim()?: "Không có dữ liệu"}"
                departmentAddressText.text = "${department.address?.trim()?: "Không có dữ liệu"}"
            }
        }
    }

    interface OnItemClickListener {
        fun onEmailClick(department: Department)
        fun onPhoneClick(department: Department)
        fun onListEquipClick(department: Department)
    }

    companion object{
        private val DEPARTMENT_COMPARATOR = object : DiffUtil.ItemCallback<Department>() {
            override fun areItemsTheSame(oldItem: Department, newItem: Department): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Department, newItem: Department): Boolean {
                return oldItem == newItem
            }
        }
    }
}