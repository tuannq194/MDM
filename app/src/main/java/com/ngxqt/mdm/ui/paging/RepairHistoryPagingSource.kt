package com.ngxqt.mdm.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngxqt.mdm.data.model.objectmodel.Equipment
import com.ngxqt.mdm.data.remote.ApiInterface
import retrofit2.HttpException
import java.io.IOException

class RepairHistoryPagingSource(
    private val mdmApi: ApiInterface,
    private val authorization: String,
    private val equipmentId: Int?
) : PagingSource<Int, Equipment>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Equipment> {
        return try {
            val response = mdmApi.getRepairHistory(
                authorization,
                equipmentId
            )
            if (response.body()?.success == true) {
                response.body()?.let { body ->
                    var data: MutableList<Equipment> = mutableListOf()
                    body.data?.equipment?.let { data = body.data.equipment }
                    LoadResult.Page(
                        data = data,
                        prevKey = null,
                        nextKey = null
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