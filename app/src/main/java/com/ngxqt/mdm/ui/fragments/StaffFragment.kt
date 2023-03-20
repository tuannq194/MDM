package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentStaffBinding
import com.ngxqt.mdm.ui.adapters.StaffAdapter
import com.ngxqt.mdm.ui.viewmodels.StaffViewModel
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StaffFragment : Fragment() {
    private val viewModel: StaffViewModel by viewModels()
    private var _binding: FragmentStaffBinding? = null
    private val binding get() = _binding!!
    private val staffAdapter = StaffAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStaffBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getAllUsers()
        binding.btnStaffSearch.setOnClickListener {
            val keyword = binding.editTextStaffSearch.text.toString().trim()
            if (keyword.isNotEmpty()){
                searchUser(keyword)
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Nhân Viên")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewStaff.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = staffAdapter
        }
    }

    private fun getAllUsers(){
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllUsers(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllUsersResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        staffAdapter.submitList(it.data?.data)
                        binding.tvStaffError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.tvStaffError.visibility = View.VISIBLE
                        binding.tvStaffError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLUSERS_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    private fun searchUser(keyword: String) {
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.searchUsers(it,keyword) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.searchUsersResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.tvStaffError.visibility = View.GONE
                        val data = it.data?.data
                        if (data?.isNotEmpty() == true){
                            staffAdapter.submitList(data)
                            binding.tvStaffError.visibility = View.GONE
                        }else{
                            Toast.makeText(requireContext(), "Không Tìm Thấy Nhân Viên Có Từ Khóa", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.tvStaffError.visibility = View.VISIBLE
                        binding.tvStaffError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLEQUIP_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }
}