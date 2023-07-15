package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.databinding.FragmentInventoryBinding
import com.ngxqt.mdm.ui.adapters.EquipmentsPagingAdapter
import com.ngxqt.mdm.ui.adapters.ItemLoadStateAdapter
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InventoryFragment : Fragment(),
    EquipmentsPagingAdapter.OnItemClickListener {
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!
    private val equipmentsPagingAdapter = EquipmentsPagingAdapter(this)
    private var filterKeyword: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.btnInventoryScan.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryFragment_to_scanFragment)
        }

        binding.btnInventorySearch.setOnClickListener {
            filterKeyword = binding.editTextEquipmentsSearch.text.toString().trim()
            if (filterKeyword!!.isNotEmpty()){
                getEquipments(null,filterKeyword)
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Kiểm Kê")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewInventory.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = equipmentsPagingAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadStateAdapter { equipmentsPagingAdapter.retry() },
                footer = ItemLoadStateAdapter { equipmentsPagingAdapter.retry() }
            )
        }
        binding.buttonRetry.setOnClickListener {
            equipmentsPagingAdapter.retry()
            getEquipments(null,filterKeyword)
        }
        equipmentsPagingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                imageFind.isVisible = false
                textViewFind.isVisible = false
                paginationProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewInventory.isVisible = loadState.source.refresh is LoadState.NotLoading
                cardViewRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                imageError.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.Error) {
                    val errorState = loadState.source.refresh as LoadState.Error
                    textViewError.text = "${errorState.error.message}"
                }

                //Empty View
                if(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && equipmentsPagingAdapter.itemCount <= 0){
                    recyclerViewInventory.isVisible = false
                    imageEmpty.isVisible = true
                    textViewEmpty.isVisible = true
                    imageFind.isVisible = false
                    textViewFind.isVisible = false
                } else {
                    imageEmpty.isVisible = false
                    textViewEmpty.isVisible = false
                }
            }
        }
    }

    private fun getEquipments(status: Int?, name: String?, departmentId: Int? = null){
        // Call API
        lifecycleScope.launch {
            UserPreferences(requireContext()).accessTokenString()?.let { token ->
                viewModel.getEquipments(
                    authorization = token,
                    name = name,
                    departmentId = departmentId,
                    statusId = status
                ).observe(viewLifecycleOwner){
                    equipmentsPagingAdapter.submitData(lifecycle,it)
                }
            }
        }
    }

    override fun onItemClick(equipment: Equipment) {
        val action = InventoryFragmentDirections.actionInventoryFragmentToInventoryNoteFragment(equipment)
        findNavController().navigate(action)
    }
}