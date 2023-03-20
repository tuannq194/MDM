package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.Department
import com.ngxqt.mdm.databinding.FragmentDepartmentBinding
import com.ngxqt.mdm.ui.adapters.DepartmentAdapter
import com.ngxqt.mdm.ui.viewmodels.DepartmentViewModel
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DepartmentFragment : Fragment(), DepartmentAdapter.OnItemClickListener {
    private val viewModel: DepartmentViewModel by viewModels()
    private var _binding: FragmentDepartmentBinding? = null
    private val binding get() = _binding!!
    private val departmentAdapter = DepartmentAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDepartmentBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getAllDepartments()
    }
    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Khoa Phòng")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewDepartment.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = departmentAdapter
        }
    }

    private fun getAllDepartments(){
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllDepartments(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllDepartmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        departmentAdapter.submitList(it.data?.data)
                        binding.tvDepartmentError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.tvDepartmentError.visibility = View.VISIBLE
                        binding.tvDepartmentError.setText("ERROR ${it.message}\nHÃY THỬ KIỂM TRA KẾT NỐI INTERNET")
                        Log.e("GETALLDEPARTMENT_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }

    override fun onEmailClick(department: Department) {
        Toast.makeText(requireContext(),"Email", Toast.LENGTH_SHORT).show()
    }

    override fun onPhoneClick(department: Department) {
        Toast.makeText(requireContext(),"Phone", Toast.LENGTH_SHORT).show()
    }

    override fun onListEquipClick(department: Department) {
        val action = DepartmentFragmentDirections.actionDepartmentFragmentToDepartmentEquipmentFragment(department)
        findNavController().navigate(action)
    }
}