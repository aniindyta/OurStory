package com.example.storyapp.view.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.retrofit.UserModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class PostStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun postStory(file: MultipartBody.Part, description: RequestBody) = storyRepository.uploadStory(file, description)

    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }
}