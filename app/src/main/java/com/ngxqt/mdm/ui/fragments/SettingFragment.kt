package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.databinding.FragmentSettingBinding
import com.ngxqt.mdm.ui.viewmodels.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private val viewModel: SettingViewModel by viewModels()
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToggleButton()
        binding.settingSavePassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.saveSettingPassword(true)
            } else {
                viewModel.saveSettingPassword(false)
            }
        }
        binding.settingBiometric.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(context,"Bật xác thực vân tay", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,"Tắt xác thực vân tay", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setToggleButton(){
        UserPreferences(requireContext()).accessSettingPassword.asLiveData().observe(viewLifecycleOwner, Observer { isSaved ->
            binding.settingSavePassword.isChecked = isSaved == true
        })
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Cài Đặt")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

}