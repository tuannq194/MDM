package com.ngxqt.mdm.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngxqt.mdm.data.model.Department
import com.ngxqt.mdm.data.remote.ApiInterface
import retrofit2.HttpException
import java.io.IOException

class DepartmentsPagingSource(
    private val mdmApi: ApiInterface,
    private val authorization: String,
    private val keyword: String?
) : PagingSource<Int,Department>() {
    companion object {
        private const val STARTING_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Department> {
        val page = params.key ?: STARTING_INDEX

        return try {
            val response = mdmApi.getDepartments(
                authorization,
                page,
                keyword
            )
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    var data: MutableList<Department> = mutableListOf()
                    body.data?.departments?.rows?.let { data = body.data.departments.rows }
                    LoadResult.Page(
                        data = data,
                        prevKey = if (page == STARTING_INDEX) null else page - 1,
                        nextKey = if (data.isEmpty()) null else page + 1
                    )
                } ?: run {
                    LoadResult.Error(NullPointerException("Response body is null"))
                }
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Department>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}