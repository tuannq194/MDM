package com.ngxqt.mdm.ui.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ngxqt.mdm.data.model.objectmodel.Notification
import com.ngxqt.mdm.data.remote.ApiInterface
import retrofit2.HttpException
import java.io.IOException

class NotificationPagingSource(
    private val mdmApi: ApiInterface,
    private val authorization: String
) : PagingSource<Int,Notification>() {
    companion object {
        private const val STARTING_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Notification> {
        val page = params.key ?: STARTING_INDEX

        return try {
            val response = mdmApi.getNotification(
                authorization,
                page
            )
            if (response.body()?.success == true) {
                response.body()?.let { body ->
                    var data: MutableList<Notification> = mutableListOf()
                    body.data?.notifications?.rows?.let { data = body.data.notifications.rows }
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

    override fun getRefreshKey(state: PagingState<Int, Notification>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}