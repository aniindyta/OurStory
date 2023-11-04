package com.example.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.response.main.ListStoryItem
import com.example.storyapp.data.retrofit.UserModel
import kotlinx.coroutines.launch

class MainViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return storyRepository.getStories().cachedIn(viewModelScope)
    }

    fun getSession(): LiveData<UserModel> {
        return storyRepository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            storyRepository.logout()
        }
    }
}