package com.example.storyapp.di

import android.content.Context
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.data.retrofit.ApiConfig
import com.example.storyapp.data.retrofit.UserPreference
import com.example.storyapp.data.retrofit.dataStore
import com.example.storyapp.database.StoryDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val storyDatabase = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, pref, storyDatabase)
    }
}