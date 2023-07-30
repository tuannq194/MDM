package com.ngxqt.mdm.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.databinding.DialogBottomBinding
import com.ngxqt.mdm.ui.adapters.HistoryPagingAdapter
import com.ngxqt.mdm.ui.adapters.ItemLoadStateAdapter
import com.ngxqt.mdm.ui.viewmodels.DialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyDialog() : DialogFragment() {
    private val viewModel: DialogViewModel by viewModels()
    private lateinit var itemSelectedlistener: OnPickerItemSelectedListener
    private lateinit var confirmClicklistener: OnConfirmClickListener
    private var title: String? = null
    private var description: String? = null
    private var items: MutableList<String>? = null
    private var equipment: Equipment? = null

    constructor(equipment: Equipment, title: String) : this() {
        this.equipment = equipment
        this.title = title
    }
    constructor(items: MutableList<String>, title: String, listener: OnPickerItemSelectedListener) : this() {
        this.items = items
        this.title = title
        this.itemSelectedlistener = listener
    }

    constructor(title: String, description: String? = null, listener: OnConfirmClickListener) : this() {
        this.title = title
        this.description = description
        this.confirmClicklistener = listener
    }

    companion object {
        const val FILTER_DIALOG = "filterEquipments"
        const val INVENTORY_HISTORY_DIALOG = "inventoryHistory"
        const val REPAIR_HISTORY_DIALOG = "repairHistory"
        const val CONFIRM_DIALOG = "confirm"
    }

    private var _binding: DialogBottomBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBottomBinding.inflate(LayoutInflater.from(activity))
        var dialog: Dialog? = null
        if (tag.equals(FILTER_DIALOG)) dialog = filterEquipmentsDialog()
        else if (tag.equals(INVENTORY_HISTORY_DIALOG)) dialog = inventoryHistoryDialog()
        else if (tag.equals(REPAIR_HISTORY_DIALOG)) dialog = repairHistoryDialog()
        else if (tag.equals(CONFIRM_DIALOG)) dialog = confirmDialog()
        dialog?.apply {
            show()
            window!!.apply {
                setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                attributes.windowAnimations = R.style.DialogAnimation
                setGravity(Gravity.BOTTOM)
            }
        }
        return dialog!!
    }

    private fun filterEquipmentsDialog(): Dialog? {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyDialogStyle)
        builder.setView(binding.root)

        binding.apply {
            dialogTitle.setText("${title}")
            dialogRecyclerView.visibility= View.GONE
            dialogPicker.visibility = View.VISIBLE
            dialogPicker.maxValue = items?.size?.minus(1) ?: 0
            dialogPicker.displayedValues = items?.toTypedArray()
            dialogPicker.wrapSelectorWheel = true
            dialogOk.setOnClickListener {
                itemSelectedlistener?.onPickerItemSelected(binding.dialogPicker.value)
                dialog?.dismiss()
            }
        }

        return builder.create()
    }

    private fun inventoryHistoryDialog(): Dialog? {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyDialogStyle)
        builder.setView(binding.root)

        val historyPagingAdapter = HistoryPagingAdapter(INVENTORY_HISTORY_DIALOG)
        setupRecyclerView(historyPagingAdapter)
        lifecycleScope.launch {
            UserPreferences(requireContext()).accessTokenString()?.let { token ->
                viewModel.getInventoryHistory(
                    token,
                    equipment?.id
                ).observe(this@MyDialog) {
                    historyPagingAdapter.submitData(lifecycle, it)
                }
            }
        }
        binding.apply {
            dialogTitle.setText("${title}")
            dialogPicker.visibility = View.GONE
            dialogRecyclerView.visibility= View.VISIBLE
            dialogOk.setOnClickListener {
                dialog?.dismiss()
            }
        }

        return builder.create()
    }

    private fun repairHistoryDialog(): Dialog? {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyDialogStyle)
        builder.setView(binding.root)

        val historyPagingAdapter = HistoryPagingAdapter(REPAIR_HISTORY_DIALOG)
        setupRecyclerView(historyPagingAdapter)
        lifecycleScope.launch {
            UserPreferences(requireContext()).accessTokenString()?.let { token ->
                viewModel.getRepairHistory(
                    token,
                    equipment?.id
                ).observe(this@MyDialog) {
                    historyPagingAdapter.submitData(lifecycle, it)
                }
            }
        }
        binding.apply {
            dialogTitle.setText("${title}")
            dialogPicker.visibility = View.GONE
            dialogRecyclerView.visibility= View.VISIBLE
            dialogOk.setOnClickListener {
                dialog?.dismiss()
            }
        }

        return builder.create()
    }

    private fun confirmDialog(): Dialog? {
        val builder = AlertDialog.Builder(requireActivity(), R.style.MyDialogStyle)
        builder.setView(binding.root)

        binding.apply {
            dialogTitle.setText("${title}")
            dialogPicker.visibility = View.GONE
            dialogRecyclerView.visibility = View.GONE
            dialogCancel.visibility = View.VISIBLE
            dialogCancel.setOnClickListener {
                dialog?.dismiss()
            }
            dialogOk.setOnClickListener {
                confirmClicklistener.onConfirmClick(true)
                dialog?.dismiss()
            }
        }

        return builder.create()
    }

    private fun setupRecyclerView(historyPagingAdapter: HistoryPagingAdapter) {
        binding.dialogRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            adapter = historyPagingAdapter.withLoadStateHeaderAndFooter(
                header = ItemLoadStateAdapter { historyPagingAdapter.retry() },
                footer = ItemLoadStateAdapter { historyPagingAdapter.retry() }
            )
        }

        historyPagingAdapter.addLoadStateListener { loadState ->
            binding.apply {
                paginationProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
                dialogRecyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                textViewError.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.Error) {
                    val errorState = loadState.source.refresh as LoadState.Error
                    textViewError.text = "${errorState.error.message}"
                }

                //Empty View
                if (loadState.source.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached
                    && historyPagingAdapter.itemCount <= 0
                ) {
                    dialogRecyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }
            }
        }

    }

    interface OnPickerItemSelectedListener {
        fun onPickerItemSelected(position: Int)
    }

    interface OnConfirmClickListener {
        fun onConfirmClick(clicked: Boolean)
    }
}