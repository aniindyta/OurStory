package com.example.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.retrofit.UserModel

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoryDetail(storyId: String) = storyRepository.getStoryDetail(storyId)

    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }
}