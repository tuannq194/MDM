package com.ngxqt.mdm

class Draft {
    /*private fun getDepartmentsResponse() {
        //Get LiveData
        viewModel.getAllDepartmentsResponseLiveData.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { it ->
                binding.paginationProgressBar.visibility = View.INVISIBLE
                when (it) {
                    is Resource.Success -> {
                        val data = it.data?.data
                        val count = data?.count
                        count?.let {
                            data?.departments?.rows?.let { mutableListDepartment?.addAll(it) }
                            if (count > pageDepartments * 10) {
                                pageDepartments++
                                getAllDepartment()
                            } else buttonDepartmentClickable = true
                        }
                    }
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Log.e("GETALLDEPARTMENT_OBSERVER_ERROR", it.data.toString())
                    }
                }
            }
        })
    }*/
}