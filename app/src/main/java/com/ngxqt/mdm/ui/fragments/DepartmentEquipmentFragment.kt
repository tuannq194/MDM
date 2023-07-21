package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.databinding.FragmentDepartmentEquipmentBinding
import com.ngxqt.mdm.ui.adapters.EquipmentsPagingAdapter
import com.ngxqt.mdm.ui.adapters.ItemLoadStateAdapter
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DepartmentEquipmentFragment : Fragment(), EquipmentsPagingAdapter.OnItemClickListener {
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentDepartmentEquipmentBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<DepartmentEquipmentFragmentArgs>()
    private val equipmentsPagingAdapter = EquipmentsPagingAdapter(this)
    private var isFirstRendered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDepartmentEquipmentBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        args.department.name?.let { binding.titleDepartmentEquipments.text = "$it" }
        if (!isFirstRendered){
            getDepartmentEquipments()
            isFirstRendered = true
        }
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Danh Sách Thiết Bị")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewDepartmentEquipments.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = equipmentsPagingAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadStateAdapter { equipmentsPagingAdapter.retry() },
                footer = ItemLoadStateAdapter { equipmentsPagingAdapter.retry() }
            )
        }
        binding.buttonRetry.setOnClickListener {
            equipmentsPagingAdapter.retry()
            getDepartmentEquipments()
        }
        equipmentsPagingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                paginationProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewDepartmentEquipments.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                imageError.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.Error) {
                    val errorState = loadState.source.refresh as LoadState.Error
                    textViewError.text = "${errorState.error.message}"
                }

                //Empty View
                if(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && equipmentsPagingAdapter.itemCount <= 0){
                    recyclerViewDepartmentEquipments.isVisible = false
                    imageEmpty.isVisible = true
                    textViewEmpty.isVisible = true
                } else {
                    imageEmpty.isVisible = false
                    textViewEmpty.isVisible = false
                }
            }
        }

    }

    private fun getDepartmentEquipments() {
        val department = args.department
        // Call API
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getEquipments(it, departmentId = department.id).observe(viewLifecycleOwner){
                binding.recyclerViewDepartmentEquipments.adapter = equipmentsPagingAdapter
                equipmentsPagingAdapter.submitData(lifecycle,it)
            }}
        }
    }

    override fun onItemClick(equipment: Equipment) {
        equipment.id?.let {
            val action = DepartmentEquipmentFragmentDirections.actionDepartmentEquipmentFragmentToEquipmentDetailFragment(it)
            findNavController().navigate(action)
        }
    }
}