package com.ngxqt.mdm.ui.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ngxqt.mdm.R
import com.ngxqt.mdm.databinding.DialogBottomBinding
import com.ngxqt.mdm.ui.adapters.DialogRecyclerViewAdapter

class MyDialog () : DialogFragment(){
    private lateinit var listener: onPickerItemSelectedListener
    private var dialogRecyclerViewAdapter: DialogRecyclerViewAdapter? = null
    private var dialogTitle: String? = null
    private var items: List<String>? = null

    constructor(adapter: DialogRecyclerViewAdapter, title: String, listener: onPickerItemSelectedListener) : this() {
        this.dialogRecyclerViewAdapter = adapter
        this.dialogTitle = title
        this.listener = listener
    }
    constructor(items: List<String>, title: String, listener: onPickerItemSelectedListener) : this() {
        this.items = items
        this.dialogTitle = title
        this.listener = listener
    }

    companion object {
        const val FILTER_DIALOG = "filterEquipments"
    }

    private var _bindingDialog: DialogBottomBinding? = null
    private val bindingDialog get() = _bindingDialog!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        var dialog: Dialog? = null
        if (tag.equals(FILTER_DIALOG)) dialog = filterEquipmentsDialog()
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
        _bindingDialog = DialogBottomBinding.inflate(LayoutInflater.from(activity))
        builder.setView(bindingDialog.root)

        bindingDialog.apply {
            dialogTitle.setText("Lọc thiết bị")
            dialogPicker.maxValue = items?.size?.minus(1) ?: 0
            dialogPicker.displayedValues = items?.toTypedArray()
            dialogPicker.wrapSelectorWheel = false
            dialogOk.setOnClickListener {
                val selected = items?.get(bindingDialog.dialogPicker.value).toString().let {
                    if (it == "Đang Sử Dụng") {"active"}
                    else if (it == "Đang Báo Hỏng") {"was_broken"}
                    else if (it == "Đang Sửa Chữa") {"corrected"}
                    else if (it == "Đã Thanh Lý") {"liquidated"}
                    else if (it == "Ngưng Sử Dụng") {"inactive"}
                    else if (it == "Mới") {"not_handed"}
                    else if (it == "Tất Cả") {null}
                    else {null}
                }
                listener?.onPickerItemSelected(selected)
                dialog?.dismiss()
            }
        }

        return builder.create()
    }

    interface onPickerItemSelectedListener{
        fun onPickerItemSelected(status: String?)
    }
}