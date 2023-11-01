package com.example.storyapp.view.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.storyapp.data.Result
import com.example.storyapp.data.response.main.ListStoryItem
import com.example.storyapp.databinding.ActivityDetailStoryBinding
import com.example.storyapp.view.ViewModelFactory

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var detailStoryBinding: ActivityDetailStoryBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailStoryBinding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(detailStoryBinding.root)

        @Suppress("DEPRECATION")
        val story = intent.getParcelableExtra<ListStoryItem>("STORY_DATA")

        viewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                if (story != null) {
                    detailStoryBinding.tvName.text = story.name
                    detailStoryBinding.tvDesc.text = story.description
                    Glide.with(this).load(story.photoUrl).into(detailStoryBinding.imgItemImage)
                    viewModel.getStoryDetail(story.id).observe(this) { storyResult ->
                        val alertDialog = AlertDialog.Builder(this)
                        when (storyResult) {
                            is Result.Loading -> {
                                detailStoryBinding.progressBar.visibility = View.VISIBLE
                            }

                            is Result.Success -> {
                                detailStoryBinding.progressBar.visibility = View.GONE
                            }

                            is Result.Error -> {
                                detailStoryBinding.progressBar.visibility = View.GONE
                                val errorMessage = storyResult.error
                                alertDialog.apply {
                                    setTitle("Ups!")
                                    setMessage("$errorMessage.")
                                    setPositiveButton("Retry") { _, _ ->

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}