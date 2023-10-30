package com.example.storyapp.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.view.ViewModelFactory
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.data.retrofit.UserModel
import com.example.storyapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            loginViewModel.login(email, password).observe(this) { result ->
                if (!isFinishing) {
                    val alertDialog = AlertDialog.Builder(this)
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            loginViewModel.saveSession(UserModel(result.data.loginResult!!.name, email,result.data.loginResult!!.token))
                            Log.d("TOKEN", "login: ${result.data.loginResult.token}")
                            val intent = Intent(this, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
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
                    if (!isFinishing) {
                        alertDialog.create().show()
                    }
                }
            }
        }
    }
}