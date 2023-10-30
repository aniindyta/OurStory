package com.example.storyapp.view.regis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityRegisterBinding
import com.example.storyapp.view.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.regisButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            registerViewModel.register(name, email, password).observe(this) { result ->
                val alertDialog = AlertDialog.Builder(this)
                when (result) {
                    is Result.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        alertDialog.apply {
                            setTitle("Yeay!")
                            setMessage("Your account with the email $email is ready. Let's log in and discover exciting stories, and share your experiences on Dicoding.")
                            setPositiveButton("Continue") { _, _ ->
                                finish()
                            }
                        }
                    }

                    is Result.Error -> {
                        binding.progressBar.visibility = View.GONE
                        val errorMessage = result.error
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
        }
    }
}