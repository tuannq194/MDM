package com.ngxqt.mdm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ngxqt.mdm.data.model.GetDepartmentByIdResponse
import com.ngxqt.mdm.util.Event
import com.ngxqt.mdm.util.NetworkUtil
import com.ngxqt.mdm.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class Draft {
/*private fun getAllEquipments(){
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllEquipments(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        equipmentsAdapter.submitList(it.data?.data)
                        binding.tvEquipmentsError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentsError.visibility = View.VISIBLE
                        binding.tvEquipmentsError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun searchEquipments(keyword: String) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.searchEquipments(it,keyword) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.searchEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.tvEquipmentsError.visibility = View.GONE
                        val data = it.data?.data
                        if (data?.isNotEmpty() == true){
                            binding.recyclerViewEquipments.adapter = equipmentsAdapter
                            equipmentsAdapter.submitList(data)
                            binding.tvEquipmentsError.visibility = View.GONE
                        }else{
                            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị Chứa Từ Khóa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentsError.visibility = View.VISIBLE
                        binding.tvEquipmentsError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun statisticalEquipments(status: String) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            binding.paginationProgressBar.visibility = View.VISIBLE
            userPreferences.accessTokenString()?.let { viewModel.statisticalEquipments(it,status) }
        }
        //Get LiveData
        viewModel.statisticalEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.tvEquipmentsError.visibility = View.GONE
                        val data = it.data?.equipment
                        if (data?.isNotEmpty() == true){
                            equipmentsAdapter.submitList(data)
                            binding.tvEquipmentsError.visibility = View.GONE
                        }else{
                            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị Chứa Từ Khóa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvEquipmentsError.visibility = View.VISIBLE
                        binding.tvEquipmentsError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }*/
}