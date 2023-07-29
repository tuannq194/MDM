package com.ngxqt.mdm.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ngxqt.mdm.R
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.databinding.FragmentStatisticBinding
import com.ngxqt.mdm.ui.adapters.LegendAdapter
import com.ngxqt.mdm.ui.dialog.MyDialog
import com.ngxqt.mdm.ui.model.LegendItemModel
import com.ngxqt.mdm.ui.viewmodels.StatisticViewModel
import com.ngxqt.mdm.util.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StatisticFragment : Fragment() {
    private val viewModel: StatisticViewModel by viewModels()
    private var _binding: FragmentStatisticBinding? = null
    private val binding get() = _binding!!
    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private var mutableListEquipment: MutableList<Equipment>? = mutableListOf()
    private var buttonClickable = false
    private var isFirstRendered = false
    private var statusName: String? = null
    private var textButtonStatus: String? = null
    private var statisticType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticBinding.inflate(inflater, container, false)
        val view = binding.root
        setToolbar()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isFirstRendered){
            statisticType = StatisticType.STATUS.typeName
            textButtonStatus = statusIdToStatusNameMapper(0)
            getAllEquipment()
            isFirstRendered = true
        }
        binding.btnStatisticType.apply {
            setText("Trạng Thái")
            setOnClickListener {
                showDialogSelectType()
            }
        }

        binding.btnStatisticStatus.apply {
            setText(textButtonStatus)
            setOnClickListener {
                if (buttonClickable && mutableListEquipment != null) showDialogStatus()
                else Toast.makeText(requireContext(),"Đợi Tải Dữ Liệu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDialogSelectType() {
        val statusList = mutableListOf(
            StatisticType.STATUS.typeName,
            StatisticType.RISK.typeName
        )
        val dialog = MyDialog(statusList,"Chọn Loại Dữ Liệu Cần Thống Kê", object : MyDialog.OnPickerItemSelectedListener{
            override fun onPickerItemSelected(position: Int) {
                statisticType = statusList.get(position)
                if (position == 0) {
                    binding.layoutButtonStatus.visibility = View.VISIBLE
                    setupPieChart(EquipmentStatusEnum.ALL.id)
                }
                else if (position == 1) {
                    binding.layoutButtonStatus.visibility = View.GONE
                    setupPieChart(null)
                }
                binding.btnStatisticType.setText(statisticType)
            }
        })
        dialog.show(parentFragmentManager, MyDialog.FILTER_DIALOG)
    }

    private fun showDialogStatus(){
        val statusList = mutableListOf(
            EquipmentStatusEnum.ALL.statusName,
            EquipmentStatusEnum.ACTIVE.statusName,
            EquipmentStatusEnum.WAS_BROKEN.statusName,
            EquipmentStatusEnum.REPAIRED.statusName,
            EquipmentStatusEnum.INACTIVE.statusName,
            EquipmentStatusEnum.LIQUIDATED.statusName
        )
        val dialog = MyDialog(statusList,"Thống Kê Trạng Thái", object : MyDialog.OnPickerItemSelectedListener{
            override fun onPickerItemSelected(position: Int) {
                statusName = statusList.get(position)
                textButtonStatus = statusName
                binding.btnStatisticStatus.setText(textButtonStatus)
                setupPieChart(statusNameToStatusIdMapper(statusName))
            }
        })
        dialog.show(parentFragmentManager, MyDialog.FILTER_DIALOG)
    }

    private fun setupPieChart(statusId: Int?) {
        pieChart = binding.statisticPieChart
        pieChart.apply {
            visibility = View.VISIBLE
            description.isEnabled = false // Miêu tả (description) của Pie Chart
            legend.isEnabled = false

            centerText = when(statisticType) {
                StatisticType.STATUS.typeName -> {
                    if (statusId == EquipmentStatusEnum.ALL.id) { // Đặt text giữa
                        "Tất Cả\nTrạng Thái"
                    } else {
                        "Thiết Bị\n${statusIdToStatusNameMapper(statusId)}"
                    }
                }
                StatisticType.RISK.typeName -> "Mức Độ\nRủi Ro"
                else -> ""
            }

            setCenterTextSize(16f) // Cỡ chữ text giữa

            setHoleRadius(40f) // Độ rộng bán kinh của lỗ giữa, tính theo phần trăm so với cả bán kính biểu đồ
            setTransparentCircleRadius(43f)
            animate()
        }

        // Dữ liệu biểu đồ
        val pieEntries = when(statisticType) {
            StatisticType.STATUS.typeName -> getStatusPieEntries(statusId)
            StatisticType.RISK.typeName -> getRiskPieEntries()
            else -> ArrayList<PieEntry>()
        }



        // Khởi tạo PieDataSet với danh sách PieEntry và chuỗi mô tả "Colors"
        val pieDataSet = PieDataSet(pieEntries, "Colors")
        val customColors = listOf(
            Color.parseColor("#2f71b3"),
            Color.parseColor("#d13425"),
            Color.parseColor("#faa01c"),
            Color.parseColor("#449809"),
            Color.parseColor("#747146")
        )
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS.toList())
        pieDataSet.setValueTextColor(Color.BLACK)

        // Khởi tạo PieData với PieDataSet
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(14f)
        pieData.setValueFormatter(object : ValueFormatter() { // Sử dụng ValueFormatter tùy chỉnh để hiển thị số lượng là số nguyên kèm phần trăm
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}"
            }
        })

        // Cài đặt danh sách legend
        setupLegendRecyclerView(pieEntries, pieDataSet)

        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun getStatusPieEntries(statusId: Int?): ArrayList<PieEntry> {
        // Dữ liệu biểu đồ
        val pieEntries = ArrayList<PieEntry>()

        // Tạo một HashMap để lưu trữ số lượng Equipment cho mỗi tên department
        val equipmentCountByDepartmentName = mutableMapOf<String, Int>()

        // Duyệt qua danh sách mutableListEquipment và đếm số lượng theo tên department
        for (equipment in mutableListEquipment!!) {
            if (statusId == 0 || equipment.statusId == statusId) {
                val departmentName = equipment.department?.name ?: "Khoa Phòng Khác"
                // Kiểm tra nếu tên department đã có trong HashMap, nếu có thì tăng giá trị lên 1, nếu không thì thêm mới với giá trị 1
                equipmentCountByDepartmentName[departmentName] = equipmentCountByDepartmentName.getOrDefault(departmentName, 0) + 1
            }
        }

        // Thêm dữ liệu vào danh sách PieEntry để tạo biểu đồ Pie Chart
        for ((departmentName, count) in equipmentCountByDepartmentName) {
            pieEntries.add(PieEntry(count.toFloat(), departmentName))
        }
        return pieEntries
    }

    private fun getRiskPieEntries(): ArrayList<PieEntry> {
        // Dữ liệu biểu đồ
        val pieEntries = ArrayList<PieEntry>()

        // Tạo một HashMap để lưu trữ số lượng Equipment cho mỗi tên department
        val equipmentCountByRiskLevel = mutableMapOf<String, Int>()

        // Duyệt qua danh sách mutableListEquipment và đếm số lượng theo tên department
        for (equipment in mutableListEquipment!!) {
            equipment.equipmentRiskLevel?.name?.let {
                equipmentCountByRiskLevel[it] = equipmentCountByRiskLevel.getOrDefault(it, 0) + 1
            }
        }
        for (equipment in mutableListEquipment!!) {
            val riskLevel = equipment.equipmentRiskLevel?.name
            if (riskLevel.isNullOrEmpty()) {
                equipmentCountByRiskLevel["Không xác định"] = equipmentCountByRiskLevel.getOrDefault("Không xác định", 0) + 1
            }
        }

        // Thêm dữ liệu vào danh sách PieEntry để tạo biểu đồ Pie Chart
        for ((riskLevel, count) in equipmentCountByRiskLevel) {
            pieEntries.add(PieEntry(count.toFloat(), riskLevel))
        }
        return pieEntries
    }

    private fun setupLegendRecyclerView(pieEntries: ArrayList<PieEntry>, pieDataSet: PieDataSet) {
        val legendEntries = pieEntries.mapIndexed { index, pieEntry ->
            val departmentName = pieEntry.label
            val count = pieEntry.value.toInt()
            val statusName = statusName
            val color = pieDataSet.colors[index % pieDataSet.colors.size]
            val total = pieEntries.sumOf { it.value.toInt() }
            val percentage = String.format("%.1f", pieEntry.value / total * 100)
            LegendItemModel(departmentName, count, statusName, color, percentage)
        }
        val legendAdapter = LegendAdapter(legendEntries)
        binding.legendRecyclerView.adapter = legendAdapter
    }

    private fun setToolbar(){
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        binding.toolbar.toolbarTitle.setText("Thống Kê")
        binding.toolbar.toolbarBack.setOnClickListener { findNavController().navigateUp() }
    }

    private fun getAllEquipment(){
        // Call API Get All
        val userPreferences = UserPreferences(requireContext())
        lifecycleScope.launch {
            userPreferences.accessTokenString()?.let { viewModel.getAllEquipments(it) }
            binding.paginationProgressBar.visibility = View.VISIBLE
        }
        //Get LiveData
        viewModel.getAllEquipmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { it ->
                when(it) {
                    is Resource.Success -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        if (it.data?.success == true) {
                            mutableListEquipment = it.data.data?.equipments
                            setupPieChart(EquipmentStatusEnum.ALL.id)
                            buttonClickable = true
                        } else {
                            Toast.makeText(requireContext(),it.data?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    is Resource.Error -> {
                        binding.paginationProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(),"${it.message}", Toast.LENGTH_SHORT).show()
                        LogUtils.d("GETALLEQUIPMENT_OBSERVER_ERROR: ${it.message}")
                    }
                    is Resource.Loading -> {
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    enum class StatisticType(val id: Int, val typeName: String) {
        STATUS(0, "Trạng Thái"),
        RISK(1,  "Mức Độ Rủi Ro")
    }
}