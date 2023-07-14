package com.ngxqt.mdm.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngxqt.mdm.data.model.Equipment
import com.ngxqt.mdm.data.remote.ApiInterface
import retrofit2.HttpException
import java.io.IOException

class EquipmentsPagingSource(
    private val mdmApi: ApiInterface,
    private val authorization: String,
    private val name: String?,
    private val departmentId: Int?,
    private val statusId: Int?,
    private val typeId: Int?,
    private val riskLevel: Int?,
    private val yearInUse: Int?,
    private val yearOfManufacture: Int?
) : PagingSource<Int,Equipment>() {
    companion object {
        private const val STARTING_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Equipment> {
        val page = params.key ?: STARTING_INDEX

        return try {
            val response = mdmApi.getEquipments(
                authorization,
                page,
                name,
                departmentId,
                statusId,
                typeId,
                riskLevel,
                yearInUse,
                yearOfManufacture
            )
            if (response.body()?.success == true) {
                response.body()?.let { body ->
                    var data: MutableList<Equipment> = mutableListOf()
                    body.data?.equipments?.rows?.let { data = body.data.equipments.rows }
                    LoadResult.Page(
                        data = data,
                        prevKey = if (page == STARTING_INDEX) null else page - 1,
                        nextKey = if (data.isEmpty()) null else page + 1
                    )
                } ?: run {
                    LoadResult.Error(NullPointerException("Response body is null"))
                }
            } else {
                LoadResult.Error(Error("Lỗi ${response.body()?.code}: ${response.body()?.message}"))
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Equipment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}