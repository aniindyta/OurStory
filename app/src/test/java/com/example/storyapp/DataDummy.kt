package com.example.storyapp

import com.example.storyapp.data.response.main.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "https://upload.wikimedia.org/wikipedia/commons/7/71/20231006_Mark_%28NCT%29.jpg",
                createdAt = "2023-10-23T23:23:23Z",
                name = "Name $i",
                description = "Description $i",
                lon = i.toDouble(),
                id = "id_$i",
                lat = i.toDouble(),
            )
            items.add(story)
        }
        return items
    }
}