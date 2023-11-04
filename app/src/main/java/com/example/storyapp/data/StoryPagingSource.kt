package com.example.storyapp.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.storyapp.data.response.main.ListStoryItem
import com.example.storyapp.data.retrofit.ApiConfig
import com.example.storyapp.data.retrofit.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StoryPagingSource(private val userPreference: UserPreference) : PagingSource<Int, ListStoryItem>() {
    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val responseData = response.getStories(page, params.loadSize)

            LoadResult.Page(
                data = responseData.listStory,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (responseData.listStory.isEmpty()) null else page + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}