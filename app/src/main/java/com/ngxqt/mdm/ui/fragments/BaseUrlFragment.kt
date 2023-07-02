package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.databinding.FragmentBaseUrlBinding
import com.ngxqt.mdm.ui.viewmodels.BaseUrlViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BaseUrlFragment : Fragment() {
    private val  viewModel: BaseUrlViewModel by viewModels()
    private var _binding: FragmentBaseUrlBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseUrlBinding.inflate(inflater, container, false)
        val view = binding.root
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.saveBaseUrl("https://bvkienanhp.qltbyt.com")
        // Khởi tạo lại Api Module ở đây để Retrofit cập nhật giá trị mới của baseUrl
        Log.d("BaseUrlFragment", "RUN HERE")
        findNavController().navigate(R.id.action_baseUrlFragment5_to_loginFragment)
    }
}