package com.example.storyapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.storyapp.data.response.main.ListStoryItem
import com.example.storyapp.data.response.register.ErrorResponse
import com.example.storyapp.data.retrofit.ApiConfig
import com.example.storyapp.data.retrofit.ApiService
import com.example.storyapp.data.retrofit.UserModel
import com.example.storyapp.data.retrofit.UserPreference
import com.example.storyapp.database.StoryDatabase
import com.example.storyapp.database.StoryRemoteMediator
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
    private val storyDatabase: StoryDatabase
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun register(name: String, email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.error == true) {
                val errorMessage = response.message ?: "Unknown error"
                emit(Result.Error(errorMessage))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            val errorMessage = errorResponse.message ?: "Failed to parse error response"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun login(email: String, password: String) = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.error == true) {
                val errorMessage = response.message ?: "Unknown error"
                emit(Result.Error(errorMessage))
            } else {
                emit(Result.Success(response))
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            val errorMessage = errorResponse.message ?: "Failed to parse error response"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = StoryRemoteMediator(storyDatabase, userPreference),
            pagingSourceFactory = { storyDatabase.storyDao().allStory!! }
        ).liveData
    }

    fun getStoriesWithLocation() = liveData {
        emit(Result.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val successGetStoriesWithLocation = response.getStoriesWithLocation()
            emit(Result.Success(successGetStoriesWithLocation))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            val errorMessage = errorResponse.message ?: "Failed to parse error response"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun getStoryDetail(storyId: String) = liveData {
        emit(Result.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val successGetStoryDetail = response.getStoryDetail(storyId)
            emit(Result.Success(successGetStoryDetail))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            val errorMessage = errorResponse.message ?: "Failed to parse error response"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    fun uploadStory(file: MultipartBody.Part, description: RequestBody) = liveData {
        emit(Result.Loading)
        try {
            val user = runBlocking { userPreference.getSession().first() }
            val response = ApiConfig.getApiService(user.token)
            val successUploadStory = response.uploadImage(file, description)
            emit(Result.Success(successUploadStory))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
            val errorMessage = errorResponse.message ?: "Failed to parse error response"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error"))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
            storyDatabase: StoryDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userPreference, storyDatabase)
            }.also { instance = it }
    }
}