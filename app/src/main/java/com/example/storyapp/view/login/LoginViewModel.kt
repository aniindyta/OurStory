package com.example.storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.retrofit.UserModel
import kotlinx.coroutines.launch

class LoginViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    fun login(email: String, password:String) = storyRepository.login(email, password)

    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            storyRepository.saveSession(user)
        }
    }
}