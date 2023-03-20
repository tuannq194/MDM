package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.databinding.FragmentEquipmentsBinding
import com.ngxqt.mdm.ui.adapters.EquipmentsPagingAdapter
import com.ngxqt.mdm.ui.dialog.MyDialog
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EquipmentsFragment : Fragment(),
    //EquipmentsAdapter.OnItemClickListener,
    EquipmentsPagingAdapter.OnItemClickListener{
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentEquipmentsBinding? = null
    private val binding get() = _binding!!
    //private val equipmentsAdapter = EquipmentsAdapter(this)
    private val equipmentsPagingAdapter = EquipmentsPagingAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentsBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        getEquipments(null, null)
        binding.btnEquipmentsSearch.setOnClickListener {
            val keyword = binding.editTextEquipmentsSearch.text.toString().trim()
            if (keyword.isNotEmpty()){
                getEquipments(null,keyword)
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnEquipmentsFilter.setOnClickListener {
            showDialog()
        }
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Thiết Bị")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewEquipments.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = equipmentsPagingAdapter
        }
    }

    private fun showDialog(){
        val statusList = listOf("Đang Sử Dụng", "Đang Báo Hỏng", "Đang Sửa Chữa", "Đã Thanh Lý", "Ngưng Sử Dụng", "Mới", "Tất Cả")
        val dialog = MyDialog(statusList,"Lọc Thiết Bị", object : MyDialog.onPickerItemSelectedListener{
            override fun onPickerItemSelected(status: String?) {
                getEquipments(status,null)
            }
        })
        dialog.show(parentFragmentManager,MyDialog.FILTER_DIALOG)
    }

    private fun getEquipments(status: String?, keyword: String?){
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getEquipments(it,status,keyword,null).observe(viewLifecycleOwner){
                binding.recyclerViewEquipments.adapter = equipmentsPagingAdapter
                equipmentsPagingAdapter.submitData(lifecycle,it)
            }}
        }
    }

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

    override fun onItemClick(equipment: Equipment) {
        val action = EquipmentsFragmentDirections.actionEquipmentsFragmentToEquipmentDetailFragment(equipment)
        findNavController().navigate(action)
    }
}