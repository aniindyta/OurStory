package com.example.storyapp.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityMainBinding
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.detail.DetailStoryActivity
import com.example.storyapp.view.map.MapsActivity
import com.example.storyapp.view.story.PostStoryActivity
import com.example.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private val mainAdapter = MainAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }

            mainBinding.nameTextView.text = getString(R.string.greeting, user.name)
        }

        val layoutManager = LinearLayoutManager(this)
        mainBinding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        mainBinding.rvStory.removeItemDecoration(itemDecoration)

        getData()

        mainAdapter.setOnItemClickListener { story, optionsCompat ->
            val intent = Intent(this, DetailStoryActivity::class.java)
            intent.putExtra("STORY_DATA", story)
            startActivity(intent, optionsCompat?.toBundle())
        }

        mainBinding.logoutButton.setOnClickListener {
            showLogoutDialog()
        }

        mainBinding.mapButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }

        mainBinding.fabPost.setOnClickListener {
            startActivity(Intent(this, PostStoryActivity::class.java))
        }
    }

    private fun getData() {
        val adapter = MainAdapter()
        mainBinding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.getStories().observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showLogoutDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Logout")
            setMessage("Are you sure you want to log out?")
            setNegativeButton("Yes") { _, _ ->
                viewModel.logout()

                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            setPositiveButton("No") { _, _ ->

            }
        }
        val dialog = alertDialog.create()
        dialog.show()
    }
}