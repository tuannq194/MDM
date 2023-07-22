package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.data.model.postmodel.InventoryPost
import com.ngxqt.mdm.databinding.FragmentInventoryNoteBinding
import com.ngxqt.mdm.ui.viewmodels.InventoryNoteViewModel
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.initBiometric
import com.ngxqt.mdm.util.EquipmentStatusEnum
import com.ngxqt.mdm.util.LogUtils
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class InventoryNoteFragment : Fragment(), BiometricHelper.BiometricCallback {
    private val viewModel: InventoryNoteViewModel by viewModels()
    private var _binding: FragmentInventoryNoteBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<InventoryNoteFragmentArgs>()
    private var isSendInventoryRequest = false
    private var isTurnOnBiometric = false
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInventoryNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        biometricPrompt = initBiometric(requireActivity(), this)
        setEquipmentDetail()
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Kiếm Kê")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setEquipmentDetail(){
        val equipment = args.equipment
        UserPreferences(requireContext()).accessSettingBiometric.asLiveData().observe(viewLifecycleOwner, Observer { isTurnedOn ->
            isTurnOnBiometric = isTurnedOn == true
        })
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
            if (equipment.equipmentStatus?.id == EquipmentStatusEnum.ACTIVE.id){
                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green)
            }
            btnInventory.setOnClickListener {
                val note = binding.editTextNote.text.toString().trim()
                if (note.isNotEmpty()){
                    if (isTurnOnBiometric == true) BiometricHelper.authenticate(biometricPrompt)
                    else requestInventory(equipment, note)
                } else{
                    Toast.makeText(requireContext(), "Vui Lòng Nhập Ghi Chú Kiểm Kê", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestInventory(equipment: Equipment, note: String) {
        //call api
        lifecycleScope.launch {
            val deferredUserId = async { UserPreferences(requireContext()).accessUserInfo()?.id }
            val equipmentId = equipment.id
            val equipmentName = equipment.name
            val departmentName = equipment.department?.name
            val userId = deferredUserId.await()
            val inventoryDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'").format(Calendar.getInstance().time)
            val statusId = equipment.statusId
            val times = 1

            val requestEquipmentInventoryPost = InventoryPost(
                equipmentId = equipmentId,
                name = equipmentName,
                department = departmentName,
                inventoryCreateUserId = userId,
                inventoryDate = inventoryDate,
                status = statusId,
                times = times,
                note = note
            )
            if (isSendInventoryRequest == false) {
                UserPreferences(requireContext()).accessTokenString()?.let { viewModel.inventoryNote(it, requestEquipmentInventoryPost) }
                binding.paginationProgressBar.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(),"Đã Kiểm Kê Thiết Bị", Toast.LENGTH_SHORT).show()
            }
        }
        //lắng nghe livedate response
        viewModel.inventoryNoteResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        if (it.data?.success == true){
                            Toast.makeText(requireContext(),"Kiểm Kê Thành Công", Toast.LENGTH_SHORT).show()
                            isSendInventoryRequest = true
                            binding.tvInventoryNoteError.visibility = View.GONE
                        } else {
                            Toast.makeText(requireContext(),"Kiểm Kê Thất Bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"Kiểm Kê Thất Bại\n Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
                        LogUtils.d("INVENTORYNOTE_OBSERVER_ERROR: ${it.data}")
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onAuthenticationSuccess() {
        LogUtils.d("InventoryNoteFragment: onAuthenticationSuccess")
        val note = binding.editTextNote.text.toString().trim()
        requestInventory(args.equipment, note)
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}