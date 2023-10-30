package com.example.storyapp.view.regis

import androidx.lifecycle.ViewModel
import com.example.storyapp.data.StoryRepository

class RegisterViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun register(name: String, email: String, password: String) = storyRepository.register(name, email, password)
}