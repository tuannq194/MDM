package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentUserBinding
import com.ngxqt.mdm.ui.dialog.MyDialog
import com.ngxqt.mdm.ui.viewmodels.UserViewModel
import com.ngxqt.mdm.util.BiometricHelper
import com.ngxqt.mdm.util.BiometricHelper.authenticate
import com.ngxqt.mdm.util.BiometricHelper.initBiometric
import com.ngxqt.mdm.util.LogUtils
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment(), BiometricHelper.BiometricCallback {
    private val viewModel: UserViewModel by viewModels()
    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val biometricPrompt = initBiometric(requireActivity(), this)
        setUserInfo()
        binding.userSetting.setOnClickListener {
            UserPreferences(requireContext()).accessSettingBiometric.asLiveData().observe(viewLifecycleOwner, Observer { isTurnedOn ->
                if (isTurnedOn == true) authenticate(biometricPrompt)
                else findNavController().navigate(R.id.action_userFragment_to_settingFragment)
            })
        }
        binding.userLogout.setOnClickListener {
            showDialogConfirm()
        }
    }

    private fun showDialogConfirm() {
        val dialog = MyDialog(
            "Bạn thực sự muốn\nđăng xuất tài khoản?",
            listener = object : MyDialog.OnConfirmClickListener {
                override fun onConfirmClick(clicked: Boolean) {
                    if (clicked) {
                        lifecycleScope.launch {
                            viewModel.clearData()
                            findNavController().navigate(R.id.action_userFragment_to_baseUrlFragment5)
                        }
                    }
                }
            })
        dialog.show(parentFragmentManager, MyDialog.CONFIRM_DIALOG)
    }

    private fun setUserInfo() {
        val userPreferences = UserPreferences(requireContext())
        var departmentId: MutableLiveData<Int?> = MutableLiveData(null)
        lifecycleScope.launch {
            val user = userPreferences.accessUserInfo()
            binding.apply {
                Glide.with(root)
                    .load(user?.image)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.logo)
                    .into(userImage)
                userName.text = "${user?.name?.trim()?: "Không có dữ liệu"}"
                userPhone.text = "${user?.phone?.trim()?: "Không có dữ liệu"}"
                userRole.text = "${user?.role?.name?.trim()?: "Không có dữ liệu"}"
                userGender.text = "${user?.gender?.trim()?: "Không có dữ liệu"}"
                userEmail.text = "${user?.email?.trim()?: "Không có dữ liệu"}"
                userAddress.text = "${user?.address?.trim()?: "Không có dữ liệu"}"
            }
            departmentId.value = user?.departmentId
        }
        departmentId.observe(viewLifecycleOwner, Observer { id ->
            setUserDepartment(id)
        })
    }

    private fun setUserDepartment(departmentId: Int?) {
        // Call API Get All User
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getDepartmentById(it,departmentId) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getDepartmentByIdResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                var departmentText = "Không có dữ liệu"
                when(it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        if (it.data?.success == true) {
                            val data = it.data?.data?.department?.name
                            data?.let { departmentText = data}
                        }
                    }
                    is Resource.Error -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        LogUtils.d("GETALLDEPARTMENT_OBSERVER_ERROR: ${it.data}")
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
                binding.userDepartment.text = departmentText
            }
        })
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.menu_user).isChecked = true
        binding.toolbar.toolbarTitle.setText("Cá Nhân")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onAuthenticationSuccess() {
        LogUtils.d("UserFragment: onAuthenticationSuccess")
        findNavController().navigate(R.id.action_userFragment_to_settingFragment)
    }

    override fun onAuthenticationError(errorCode: Int, errorMessage: String) {
        Toast.makeText(requireContext(), "$errorMessage", Toast.LENGTH_SHORT).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(requireContext(), "Xác thực thất bại", Toast.LENGTH_SHORT).show()
    }
}