package com.example.storyapp.view.map

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class MapViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun getStoriesWithLocation() = storyRepository.getStoriesWithLocation()
}