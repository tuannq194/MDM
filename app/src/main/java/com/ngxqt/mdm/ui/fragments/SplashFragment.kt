package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences

class SplashFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed({
            UserPreferences(requireContext()).accessToken.asLiveData().observe(viewLifecycleOwner, Observer {
                if (it.isNullOrEmpty()){
                    findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
                } else{
                    findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
                }
            })
        }, 500)
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

}