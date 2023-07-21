package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.objectmodel.Department
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.databinding.FragmentEquipmentsBinding
import com.ngxqt.mdm.ui.adapters.EquipmentsPagingAdapter
import com.ngxqt.mdm.ui.adapters.ItemLoadStateAdapter
import com.ngxqt.mdm.ui.dialog.MyDialog
import com.ngxqt.mdm.ui.viewmodels.EquipmentsViewModel
import com.ngxqt.mdm.util.EquipmentStatusEnum
import com.ngxqt.mdm.util.LogUtils
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EquipmentsFragment : Fragment(),
    EquipmentsPagingAdapter.OnItemClickListener{
    private val viewModel: EquipmentsViewModel by viewModels()
    private var _binding: FragmentEquipmentsBinding? = null
    private val binding get() = _binding!!
    private val equipmentsPagingAdapter = EquipmentsPagingAdapter(this)
    private var mutableListDepartment: MutableList<Department>? = mutableListOf()
    private var buttonDepartmentClickable = false
    private var isFirstRendered = false
    private var filterKeyword: String? = null
    private var filterStatus: Int? = null
    private var filterDepartment: Int?  = null
    private var textButtonStatus: String? = null
    private var textButtonDepartment: String? = null

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

        if (!isFirstRendered){
            textButtonStatus = getString(R.string.tat_ca)
            textButtonDepartment = getString(R.string.tat_ca)
            getEquipments(filterStatus, filterKeyword, filterDepartment)
            getAllDepartment()
            isFirstRendered = true
        }

        setButtonClearFilter()

        binding.btnEquipmentsSearch.setOnClickListener {
            filterKeyword = binding.editTextEquipmentsSearch.text.toString().trim()
            if (filterKeyword!!.isNotEmpty()){
                getEquipments(filterStatus, filterKeyword, filterDepartment)
                setButtonClearFilter()
            } else{
                Toast.makeText(requireContext(), "Vui Lòng Nhập Thông Tin Để Tìm Kiếm", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnEquipmentsFilterStatus.apply {
            setText(textButtonStatus)
            setOnClickListener {
                showDialogStatus()
            }
        }

        binding.btnEquipmentsFilterDepartment.apply {
            setText(textButtonDepartment)
            setOnClickListener {
                if (buttonDepartmentClickable) showDialogDepartment()
                else Toast.makeText(requireContext(),"Đợi Tải Dữ Liệu", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClearFilter.setOnClickListener {
            filterKeyword = null
            filterStatus = null
            filterDepartment = null
            binding.editTextEquipmentsSearch.text.clear()
            textButtonStatus = getString(R.string.tat_ca)
            binding.btnEquipmentsFilterStatus.setText(textButtonStatus)
            textButtonDepartment = getString(R.string.tat_ca)
            binding.btnEquipmentsFilterDepartment.setText(textButtonDepartment)
            getEquipments(filterStatus, filterKeyword, filterDepartment)
            binding.btnClearFilter.visibility = View.GONE
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
            adapter = equipmentsPagingAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadStateAdapter { equipmentsPagingAdapter.retry() },
                footer = ItemLoadStateAdapter { equipmentsPagingAdapter.retry() }
            )
        }
        binding.buttonRetry.setOnClickListener {
            equipmentsPagingAdapter.retry()
            getAllDepartment()
        }
        equipmentsPagingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                paginationProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewEquipments.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                imageError.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.Error) {
                    val errorState = loadState.source.refresh as LoadState.Error
                    textViewError.text = "${errorState.error.message}"
                }

                //Empty View
                if(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && equipmentsPagingAdapter.itemCount <= 0){
                    recyclerViewEquipments.isVisible = false
                    imageEmpty.isVisible = true
                    textViewEmpty.isVisible = true
                } else {
                    imageEmpty.isVisible = false
                    textViewEmpty.isVisible = false
                }
            }
        }

    }

    private fun setButtonClearFilter(){
        if ( filterKeyword != null || filterStatus != null || filterDepartment != null){
            binding.btnClearFilter.visibility = View.VISIBLE
        } else {
            binding.btnClearFilter.visibility = View.GONE
        }
    }

    private fun showDialogStatus(){
        val statusList = mutableListOf("Tất Cả", "Đang Sử Dụng", "Đang Báo Hỏng", "Đang Sửa Chữa", "Đã Thanh Lý", "Ngưng Sử Dụng", "Mới")
        val dialog = MyDialog(statusList,"Lọc Trạng Thái", object : MyDialog.OnPickerItemSelectedListener{
            override fun onPickerItemSelected(position: Int) {
                textButtonStatus = statusList.get(position)
                binding.btnEquipmentsFilterStatus.setText(textButtonStatus)
                filterStatus = when(textButtonStatus) {
                    "Mới" -> EquipmentStatusEnum.NEW.id
                    "Đang Sử Dụng" -> EquipmentStatusEnum.ACTIVE.id
                    "Đang Báo Hỏng" -> EquipmentStatusEnum.WAS_BROKEN.id
                    "Đang Sửa Chữa" -> EquipmentStatusEnum.REPAIRED.id
                    "Đã Thanh Lý" -> EquipmentStatusEnum.LIQUIDATED.id
                    "Ngưng Sử Dụng" -> EquipmentStatusEnum.INACTIVE.id
                    "Tất Cả" -> null
                    else -> null
                }
                getEquipments(filterStatus, filterKeyword, filterDepartment)
                setButtonClearFilter()
            }
        })
        dialog.show(parentFragmentManager,MyDialog.FILTER_DIALOG)
    }

    private fun showDialogDepartment(){
        val departmentList: MutableList<String> = mutableListOf("Tất cả")
        for (i in 0 until (mutableListDepartment?.size ?: 0)){
            departmentList.add(mutableListDepartment?.get(i)?.name.toString())
        }
        val dialog = MyDialog(departmentList,"Lọc Khoa Phòng", object : MyDialog.OnPickerItemSelectedListener{
            override fun onPickerItemSelected(position: Int) {
                textButtonDepartment = departmentList.get(position)
                binding.btnEquipmentsFilterDepartment.setText(textButtonDepartment)
                filterDepartment = position.let {
                    if (it==0){null}
                    else {mutableListDepartment?.get(position-1)?.id}
                }

                getEquipments(filterStatus, filterKeyword, filterDepartment)
                setButtonClearFilter()
            }
        })
        dialog.show(parentFragmentManager,MyDialog.FILTER_DIALOG)
    }

    private fun getAllDepartment(){
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllDepartments(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllDepartmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { it ->
                when(it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        if (it.data?.success == true) {
                            mutableListDepartment = it.data?.data?.departments
                            buttonDepartmentClickable = true
                        } else {
                            Toast.makeText(requireContext(),it.data?.message,Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"${it.data?.message}",Toast.LENGTH_SHORT).show()
                        LogUtils.d("GETALLDEPARTMENT_OBSERVER_ERROR: ${it.data?.message}")
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun getEquipments(status: Int?, name: String?, departmentId: Int?){
        // Call API
        lifecycleScope.launch {
            UserPreferences(requireContext()).accessTokenString()?.let {token ->
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
        equipment.id?.let {
            val action = EquipmentsFragmentDirections.actionEquipmentsFragmentToEquipmentDetailFragment(it)
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}