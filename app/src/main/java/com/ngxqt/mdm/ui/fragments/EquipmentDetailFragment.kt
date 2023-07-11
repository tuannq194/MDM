package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.databinding.FragmentEquipmentDetailBinding
import com.ngxqt.mdm.ui.viewmodels.EquipmentDetailViewModel
import com.ngxqt.mdm.util.EquipmentStatusEnum
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EquipmentDetailFragment : Fragment() {
    private val viewModel: EquipmentDetailViewModel by viewModels()
    private var _binding: FragmentEquipmentDetailBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<EquipmentDetailFragmentArgs>()
    private var equipment: Equipment? = null

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
        val equipmentId = args.equipmentId

        setButton()
        getEquipmentById(equipmentId)
        getRepairHistory(equipmentId)
        getInventoryHistory(equipmentId)
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Thiết Bị")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setButton() {
        binding.apply {
            btnEquipDetailRepair.setOnClickListener {
                equipment?.let {
                    val action = EquipmentDetailFragmentDirections.actionEquipmentDetailFragmentToBrokenReportFragment(equipment!!)
                    findNavController().navigate(action)
                }
            }
            btnEquipDetailInventory.setOnClickListener {
                equipment?.let {
                    val action = EquipmentDetailFragmentDirections.actionEquipmentDetailFragmentToInventoryNoteFragment(equipment!!)
                    findNavController().navigate(action)
                }
            }
            btnEquipDetailRepairHistory.setOnClickListener {
                equipment?.let {
                    val action = EquipmentDetailFragmentDirections.actionEquipmentDetailFragmentToBrokenReportFragment(equipment!!)
                    findNavController().navigate(action)
                }
            }
            btnEquipDetailInventoryHistory.setOnClickListener {
                equipment?.let {
                    val action = EquipmentDetailFragmentDirections.actionEquipmentDetailFragmentToInventoryNoteFragment(equipment!!)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun getEquipmentById(equipmentId: Int) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getEquipmentById(it,equipmentId) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getEquipmentByIdResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        equipment = it.data?.data?.equipment
                        if (equipment != null){
                            binding.tvEquipmentDetailError.visibility = View.GONE
                            setEquipmentDetail(equipment!!)
                            Log.d("SEARCHEQUIPBYID_SUCCESS", "OK")
                        }else{
                            binding.btnEquipDetailInventory.visibility = View.GONE
                            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentDetailError.visibility = View.VISIBLE
                        binding.tvEquipmentDetailError.setText("ERROR\n${it.message}")
                        Log.e("SEARCHEQUIPBYID_OBSERVER_ERROR", it.data.toString())
                        binding.btnEquipDetailInventory.visibility = View.GONE
                    }
                }
            }
        })
    }

    private fun setEquipmentDetail(equipment: Equipment){
        binding.apply {
            Glide.with(root)
                .load(equipment.image)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.logo)
                .into(equipDetailImage)
            equipDetailTitle.text = "${equipment.name?: "Không có dữ liệu"}"
            equipDetailStatus.text = "${equipment.equipmentStatus?.name?.trim()?: "Không có dữ liệu"}"
            equipDetailModel.text = "${equipment.model?: "Không có dữ liệu"}"
            equipDetailSerial.text = "${equipment.serial?: "Không có dữ liệu"}"
            equipDetailYearManufacture.text = "${equipment.yearOfManufacture?: "Không có dữ liệu"}"
            equipDetailYearUse.text = "${equipment.yearInUse?: "Không có dữ liệu"}"
            equipDetailManufacturer.text = "${equipment.manufacturerId?: "Không có dữ liệu"}"
            equipDetailOrigin.text = "${equipment.manufacturingCountryId?: "Không có dữ liệu"}"
            if (equipment.equipmentStatus?.id == EquipmentStatusEnum.NEW.id
                || equipment.equipmentStatus?.id == EquipmentStatusEnum.ACTIVE.id){
                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
                btnEquipDetailRepair.visibility = View.VISIBLE
            } else {
                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
                btnEquipDetailRepair.visibility = View.GONE
            }
            btnEquipDetailInventory.visibility = View.VISIBLE
        }
    }

    private fun getRepairHistory(equipmentId: Int){
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getRepairHistory(it,equipmentId) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getRepairHisResLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        val repairInfo = it.data?.data?.repairInfo
                        if (repairInfo.isNullOrEmpty()) {
                            binding.equipDetailRepair.visibility = View.VISIBLE
                            binding.equipDetailRepairCardview.visibility = View.GONE
                        } else {
                            binding.tvEquipmentDetailError.visibility = View.GONE
                            binding.equipDetailRepair.visibility = View.GONE
                            binding.equipDetailRepairCardview.visibility = View.VISIBLE
                        }

                    }
                    is Resource.Error -> {
                        binding.tvEquipmentDetailError.visibility = View.VISIBLE
                        binding.tvEquipmentDetailError.setText("ERROR\n${it.message}")
                        Log.e("GET_REPAIR_HIS_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun getInventoryHistory(equipmentId: Int){
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getInventoryHistory(it,equipmentId,null) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getInventoryHisResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        val count = it.data?.data?.equipments?.count
                        if (count != null) {
                            if (count > 0){
                                binding.tvEquipmentDetailError.visibility = View.GONE
                                binding.equipDetailInventory.visibility = View.GONE
                                binding.equipDetailInventoryCardview.visibility = View.VISIBLE
                            } else {
                                binding.equipDetailInventory.visibility = View.VISIBLE
                                binding.equipDetailInventoryCardview.visibility = View.GONE
                            }
                        }
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentDetailError.visibility = View.VISIBLE
                        binding.tvEquipmentDetailError.setText("ERROR\n${it.message}")
                        Log.e("GET_INVENTORY_HIS_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }
}