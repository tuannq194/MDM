package com.ngxqt.mdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ngxqt.mdm.databinding.ItemLegendBinding
import com.ngxqt.mdm.ui.model.LegendItemModel

class LegendAdapter(private val legendEntries: List<LegendItemModel>) :
    RecyclerView.Adapter<LegendAdapter.LegendViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LegendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLegendBinding.inflate(inflater, parent, false)
        return LegendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LegendViewHolder, position: Int) {
        val legendEntry = legendEntries[position]
        holder.bind(legendEntry)
    }

    override fun getItemCount(): Int = legendEntries.size

    class LegendViewHolder(private val binding: ItemLegendBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(legendEntry: LegendItemModel) {
            binding.apply {
                legendEntry.color?.let { legendColor.setBackgroundColor(it) }
                legendText.text = "${legendEntry.departmentName}: ${legendEntry.count} thiết bị (${legendEntry.percentage}%)"
            }
        }
    }
}