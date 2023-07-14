package com.ngxqt.mdm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.ngxqt.mdm.databinding.FragmentNotiBinding
import com.ngxqt.mdm.ui.adapters.ItemLoadStateAdapter
import com.ngxqt.mdm.ui.adapters.NotificationPagingAdapter
import com.ngxqt.mdm.ui.viewmodels.NotificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NotiFragment : Fragment() {
    private val viewModel: NotificationViewModel by viewModels()
    private var _binding: FragmentNotiBinding? = null
    private val binding get() = _binding!!
    private val notificationPagingAdapter = NotificationPagingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotiBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        getNotification()
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Thông Báo")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewNoti.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = notificationPagingAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadStateAdapter { notificationPagingAdapter.retry() },
                footer = ItemLoadStateAdapter { notificationPagingAdapter.retry() }
            )
        }
        binding.buttonRetry.setOnClickListener {
            notificationPagingAdapter.retry()
        }
        notificationPagingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                paginationProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerViewNoti.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                imageError.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.Error) {
                    val errorState = loadState.source.refresh as LoadState.Error
                    textViewError.text = "${errorState.error.message}"
                }

                //Empty View
                if(loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && notificationPagingAdapter.itemCount <= 0){
                    recyclerViewNoti.isVisible = false
                    imageEmpty.isVisible = true
                    textViewEmpty.isVisible = true
                } else {
                    imageEmpty.isVisible = false
                    textViewEmpty.isVisible = false
                }
            }
        }
    }

    private fun getNotification(){
        // Call API
        lifecycleScope.launch {
            UserPreferences(requireContext()).accessTokenString()?.let {token ->
                viewModel.getNotification(
                    authorization = token
                ).observe(viewLifecycleOwner){
                    notificationPagingAdapter.submitData(lifecycle,it)
                }
            }
        }
    }
}