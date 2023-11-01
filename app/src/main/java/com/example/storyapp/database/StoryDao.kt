package com.example.storyapp.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.storyapp.data.response.main.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStory(story: List<ListStoryItem?>?)

    @get:Query("SELECT * FROM story")
    val allStory: PagingSource<Int, ListStoryItem>?

    @Query("DELETE FROM story")
    fun deleteAll()
}