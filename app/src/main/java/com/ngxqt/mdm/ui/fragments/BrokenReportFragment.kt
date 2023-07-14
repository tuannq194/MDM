package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
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
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.data.model.RepairPost
import com.ngxqt.mdm.databinding.FragmentBrokenReportBinding
import com.ngxqt.mdm.ui.viewmodels.BrokenReportViewModel
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.authenticate
import com.ngxqt.mdm.util.BiometricHelper.initBiometric
import com.ngxqt.mdm.util.EquipmentStatusEnum
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class BrokenReportFragment : Fragment(), BiometricHelper.BiometricCallback {
    private val viewModel: BrokenReportViewModel by viewModels()
    private var _binding: FragmentBrokenReportBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<BrokenReportFragmentArgs>()
    private var isSendRepairRequest = false
    private var isTurnOnBiometric = false
    private lateinit var biometricPrompt: BiometricPrompt

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBrokenReportBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        biometricPrompt = initBiometric(requireActivity(),this)
        setEquipmentDetail()
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Báo Hỏng")
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
            btnBrokenReport.setOnClickListener {
                val reason = binding.editTextReason.text.toString().trim()
                if (reason.isNotEmpty()){
                    if (isTurnOnBiometric == true) authenticate(biometricPrompt)
                    else equipment?.let { requestBroken(equipment, reason) }
                } else{
                    Toast.makeText(requireContext(), "Vui Lòng Nhập Lí Do Báo Hỏng", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestBroken(equipment: Equipment, reason: String) {
        //call api
        lifecycleScope.launch {
            val deferredUserId = async { UserPreferences(requireContext()).accessUserInfo()?.id }
            val equipmentId = equipment.id
            val equipmentName = equipment.name
            val departmentId = equipment.department?.id
            val departmentName = equipment.department?.name
            val userId = deferredUserId.await()
            val repairDate = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
            val statusId = equipment.statusId
            val code = "ok"
            val repairPriority = 1

            val requestEquipmentRepairPost = RepairPost(
                equipmentId = equipmentId,
                name = equipmentName,
                departmentId = departmentId,
                department = departmentName,
                reportingPersonId = userId,
                brokenReportDate = repairDate,
                reportStatus = statusId,
                code = code,
                reason = reason,
                repairPriority = repairPriority
            )
            if (isSendRepairRequest == false) {
                UserPreferences(requireContext()).accessTokenString()?.let { viewModel.brokenReport(it, requestEquipmentRepairPost) }
                binding.paginationProgressBar.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(),"Đã Báo Hỏng Thiết Bị", Toast.LENGTH_SHORT).show()
            }
        }
        //lắng nghe livedate response
        viewModel.brokenReportResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                when (it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        if (it.data?.success == true){
                            Toast.makeText(requireContext(),"Báo Hỏng Thành Công", Toast.LENGTH_SHORT).show()
                            binding.apply {
                                equipDetailStatus.text = "Đang Báo Hỏng"
                                equipDetailStatusCardview.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red)
                            }
                            isSendRepairRequest =  true
                            binding.tvBrokenReportError.visibility = View.GONE
                        } else {
                            Toast.makeText(requireContext(),"Báo Hỏng Thất Bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        binding.tvBrokenReportError.visibility = View.VISIBLE
                        binding.tvBrokenReportError.setText("ERROR\n${it.message}")
                        Log.e("BROKENREPORT_OBSERVER_ERROR", it.data.toString())
                        Toast.makeText(requireContext(),"Báo Hỏng Thất Bại", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun onAuthenticationSuccess() {
        Log.d("BrokenReportFragment","onAuthenticationSuccess")
        val reason = binding.editTextReason.text.toString().trim()
        args.equipment.let { requestBroken(args.equipment, reason) }
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}