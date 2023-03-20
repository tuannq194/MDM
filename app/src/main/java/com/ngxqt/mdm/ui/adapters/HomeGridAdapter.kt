package com.ngxqt.mdm.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.ngxqt.mdm.R
import com.ngxqt.mdm.databinding.ItemHomeBinding
import com.ngxqt.mdm.ui.model.HomeItemModel

class HomeGridAdapter(context: Context, itemModelArrayList: ArrayList<HomeItemModel?>?) :
    ArrayAdapter<HomeItemModel?>(context, 0, itemModelArrayList!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var listitemView = convertView
        if (convertView== null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false)

        }

        val homeItemModel: HomeItemModel? = getItem(position)
        val courseTV = listitemView!!.findViewById<TextView>(R.id.idItemText)
        val courseIV = listitemView.findViewById<ImageView>(R.id.idItemLogo)

        courseTV.setText(homeItemModel!!.item_name)
        courseIV.setImageResource(homeItemModel.item_logo!!)
        return listitemView

        /*val binding: ItemHomeBinding

        if (convertView== null) {
            val inflater = LayoutInflater.from(context)
            //binding = DataBindingUtil.inflate(inflater,R.layout.item_home,parent, false)
            binding = ItemHomeBinding.inflate(inflater,parent,false)
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ItemHomeBinding
        }

        binding.homeItem = getItem(position)
        //val homeItem: HomeItemModel? = getItem(position)
        //binding.idItemText.setText(homeItem?.item_name)
        //binding.idItemLogo.setImageResource(homeItem?.item_logo!!)
        return binding.root*/
    }
}