package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentEquipmentDetailBinding
import com.ngxqt.mdm.ui.adapters.InventoryAdapter
import com.ngxqt.mdm.ui.viewmodels.EquipmentDetailViewModel
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EquipmentDetailFragment : Fragment() {
    private val viewModel: EquipmentDetailViewModel by viewModels()
    private var _binding: FragmentEquipmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EquipmentDetailFragmentArgs>()
    private val inventoryAdapter = InventoryAdapter()
    private var isFirstRendered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEquipmentDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setEquipmentDetail()
        setupRecyclerView()

    }

    private fun setEquipmentDetail(){
        val equipment = args.equipment
        binding.apply {
            Glide.with(root)
                .load(equipment.image)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.logo)
                .into(equipDetailImage)
            equipDetailTitle.text = equipment.name
            equipDetailStatus.text = equipment.equipmentStatus?.name?.trim()
            equipDetailModel.text = equipment.model
            equipDetailSerial.text = equipment.serial
            equipDetailYearManufacture.text = equipment.yearOfManufacture.toString()
            equipDetailYearUse.text = equipment.yearInUse.toString()
            equipDetailManufacturer.text = equipment.manufacturerId
            equipDetailOrigin.text = equipment.manufacturingCountryId
            if (equipment.equipmentStatus?.id == 2  || equipment.equipmentStatus?.id == 3){
                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                equipDetailErrorCardview.visibility = View.GONE
                equipDetailError.visibility = View.VISIBLE
                btnEquipDetailError.visibility = View.VISIBLE
            } else {
                equipDetailError.visibility = View.GONE
                btnEquipDetailError.visibility = View.GONE
                equipDetailReason.text = "Không có thông tin"
                equipDetailDateFailure.text = "Không có thông tin"
            }

            btnEquipDetailInventory.setOnClickListener {
                val action = EquipmentDetailFragmentDirections.actionEquipmentDetailFragmentToInventoryNoteFragment(equipment)
                findNavController().navigate(action)
            }
            btnEquipDetailError.setOnClickListener {
                val action = EquipmentDetailFragmentDirections.actionEquipmentDetailFragmentToBrokenReportFragment(equipment)
                findNavController().navigate(action)
            }
        }
        if(!isFirstRendered){
            equipment.id?.let { getListInventory(it) }
            isFirstRendered = true
        }

    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Thiết Bị")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewEquipDetailInventory.apply {
            layoutManager = LinearLayoutManager(activity)
            //setHasFixedSize(true)
            adapter = inventoryAdapter
        }
    }

    private fun getListInventory(equipmentId: Int){
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getListInventory(it,equipmentId) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getListInventoryResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        val data = it.data?.data
                        if (data?.isNotEmpty() == true){
                            inventoryAdapter.submitList(data)
                            binding.tvEquipmentDetailError.visibility = View.GONE
                            val parent = binding.recyclerViewEquipDetailInventory.parent as ViewGroup
                            val itemHeight = inventoryAdapter.getItemHeight(parent)
                            binding.recyclerViewEquipDetailInventory.layoutParams.height = inventoryAdapter.itemCount * (itemHeight)
                            binding.equipDetailInventory.visibility = View.GONE
                            Log.d("INVENTORY","item count: ${inventoryAdapter.itemCount}\n" +
                                    "item height: ${itemHeight}\n" +
                                    "layout height: ${binding.recyclerViewEquipDetailInventory.layoutParams.height}")
                            inventoryAdapter.submitList(data)
                        } else {
                            binding.equipDetailInventory.visibility = View.VISIBLE
                        }
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentDetailError.visibility = View.VISIBLE
                        binding.tvEquipmentDetailError.setText("ERROR\n${it.message}")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }
}