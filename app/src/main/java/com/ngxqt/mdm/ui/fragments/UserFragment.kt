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
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentScanBinding
import com.ngxqt.mdm.databinding.FragmentUserBinding
import com.ngxqt.mdm.ui.viewmodels.DepartmentViewModel
import com.ngxqt.mdm.ui.viewmodels.LoginViewModel
import com.ngxqt.mdm.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UserFragment : Fragment() {
    private val viewModel: DepartmentViewModel by viewModels()
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

        setUserInfo()
        binding.btnLogOut.setOnClickListener {
            lifecycleScope.launch {
                viewModel.clearData()
                findNavController().navigate(R.id.action_userFragment_to_loginFragment)
            }
        }
    }

    private fun setUserInfo() {
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            val user = userPreferences.accessUserInfo()
            binding.apply {
                Glide.with(root)
                    .load(user?.profilePhotoUrl)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.logo)
                    .into(userImage)
                userName.text = "${user?.displayName?.trim()?: ""}"
                userPhone.text = "${user?.phone?.trim()?: ""}"

                userBirthday.text = "${user?.birthday?.trim()?: ""}"
                userGender.text = "${user?.gender?.trim()?: ""}"
                userEmail.text = "${user?.email?.trim()?: ""}"
                userAddress.text = "${user?.address?.trim()?: ""}"
            }
            setUserDepartment(user?.departmentId)
        }
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
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when(it) {
                    is Resource.Success -> {
                        binding.userDepartment.text = "${it.data?.data?.title?: ""}"
                        //binding.tvDepartmentError.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        //binding.tvDepartmentError.visibility = View.VISIBLE
                        //binding.tvDepartmentError.setText("ERROR\n${it.message}")
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        Log.e("GETALLDEPARTMENT_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }


    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).menu.findItem(R.id.menu_user).isChecked = true
        binding.toolbar.toolbarTitle.setText("Cá Nhân")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }
}