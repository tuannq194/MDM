package com.ngxqt.mdm.ui.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.data.remote.ApiInterface
import retrofit2.HttpException
import java.io.IOException

class UserPagingSource(
    private val mdmApi: ApiInterface,
    private val authorization: String,
    private val keyword: String?,
    private val roleId: Int?,
    private val departmentId: Int?
) : PagingSource<Int,User>() {
    companion object {
        private const val STARTING_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val page = params.key ?: STARTING_INDEX

        return try {
            val response = mdmApi.getUsers(
                authorization,
                page,
                keyword,
                roleId,
                departmentId
            )
            if (response.body()?.success == true) {
                response.body()?.let { body ->
                    var data: MutableList<User> = mutableListOf()
                    body.data?.users?.rows?.let { data = body.data.users.rows }
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

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}