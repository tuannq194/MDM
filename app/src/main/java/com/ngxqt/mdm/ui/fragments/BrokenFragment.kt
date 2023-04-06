package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.databinding.FragmentBrokenBinding
import com.ngxqt.mdm.ui.adapters.equipment.EquipmentsAdapter
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrokenFragment : Fragment(), EquipmentsAdapter.OnItemClickListener {
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentBrokenBinding? = null
    private val binding get() = _binding!!
    private val equipmentsAdapter = EquipmentsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrokenBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBrokenScan.setOnClickListener {
            findNavController().navigate(R.id.action_brokenFragment_to_scanFragment)
        }
        setupRecyclerView()

        binding.btnBrokenSearch.setOnClickListener {
            val keyword = binding.editTextEquipmentsSearch.text.toString().trim()
            if (keyword.isNotEmpty()){
                searchEquipments(keyword)
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Báo Hỏng")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewBroken.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = equipmentsAdapter
        }
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
                        binding.tvBrokenError.visibility = View.GONE
                        onResponseSuccess(it.data?.data)
                    }
                    is Resource.Error -> {
                        binding.tvBrokenError.visibility = View.VISIBLE
                        binding.tvBrokenError.setText("ERROR\n${it.message}")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun onResponseSuccess(data: MutableList<Equipment>?) {
        if (data?.isNotEmpty() == true){
            val equipments: MutableList<Equipment>? = mutableListOf()
            for (item in data){
                if (item.status == "active"){
                    equipments?.add(item)
                }
            }
            if (equipments?.isNotEmpty() == true){
                equipmentsAdapter.submitList(equipments)
            }
            else{
                Toast.makeText(requireContext(), "Hãy Nhập Thiết Bị Còn Đang Sử Dụng", Toast.LENGTH_SHORT).show()
            }

        }else{
            Toast.makeText(requireContext(), "Không Tìm Thấy Thiết Bị Chứa Từ Khóa", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onItemClick(equipment: Equipment) {
        val action = BrokenFragmentDirections.actionBrokenFragmentToBrokenReportFragment(equipment)
        findNavController().navigate(action)
    }
}