package com.example.storyapp.view.story

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityPostStoryBinding
import com.example.storyapp.utils.getImageUri
import com.example.storyapp.utils.reduceFileImage
import com.example.storyapp.utils.uriToFile
import com.example.storyapp.view.ViewModelFactory
import com.example.storyapp.view.main.MainActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class PostStoryActivity : AppCompatActivity() {
    private lateinit var postStoryBinding: ActivityPostStoryBinding
    private var currentImageUri: Uri? = null

    private val postStoryViewModel by viewModels<PostStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postStoryBinding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(postStoryBinding.root)

        postStoryBinding.cameraButton.setOnClickListener {
            startCamera()
        }

        postStoryBinding.galleryButton.setOnClickListener {
            startGallery()
        }

        postStoryBinding.uploadButton.setOnClickListener {
            uploadImage()
        }

        if (savedInstanceState != null) {
            val uriString = savedInstanceState.getString("currentImageUri")
            currentImageUri = uriString?.let { Uri.parse(it) }
            showImage()
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun uploadImage() {
        postStoryViewModel.getSession().observe(this) { user ->
            if (user.isLogin) {
                currentImageUri?.let { uri ->
                    val imageFile = uriToFile(uri, this).reduceFileImage()
                    Log.d("Image File", "showImage: ${imageFile.path}")
                    val description = postStoryBinding.descEditText.text

                    showLoading(true)

                    if (!description.isNullOrBlank()) {
                        val requestBody = description.toString().toRequestBody("text/plain".toMediaType())
                        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                        val multipartBody = MultipartBody.Part.createFormData(
                            "photo", imageFile.name, requestImageFile
                        )

                        postStoryViewModel.postStory(multipartBody, requestBody).observe(this) { result ->
                            val alertDialog = AlertDialog.Builder(this)
                            when (result) {
                                is Result.Loading -> {
                                    showLoading(true)
                                }

                                is Result.Success -> {
                                    showLoading(false)
                                    alertDialog.apply {
                                        setTitle("Yeay!")
                                        setMessage("Your story has been uploaded.")
                                        setPositiveButton("Back") { _, _ ->
                                            val intent = Intent(this@PostStoryActivity, MainActivity::class.java)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                            startActivity(intent)
                                        }
                                    }
                                }

                                is Result.Error -> {
                                    showLoading(false)
                                    val errorMessage = result.error
                                    Log.d("Error", "$errorMessage.")
                                    alertDialog.apply {
                                        setTitle("Ups!")
                                        setMessage("$errorMessage.")
                                        setPositiveButton("Retry") { _, _ ->

                                        }
                                    }
                                }
                            }
                            alertDialog.create().show()
                        }
                    } else {
                        Toast.makeText(this, "Description cannot be empty.", Toast.LENGTH_SHORT).show()
                    }
                } ?: Toast.makeText(this, "Image cannot be empty.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            postStoryBinding.imageView.setImageURI(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        postStoryBinding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentImageUri?.let { uri ->
            outState.putString("currentImageUri", uri.toString())
        }
    }
}