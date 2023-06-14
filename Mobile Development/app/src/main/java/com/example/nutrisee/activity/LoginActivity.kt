package com.example.nutrisee.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.ViewModelProvider
import com.example.nutrisee.R
import com.example.nutrisee.ViewModelFactory
import com.example.nutrisee.data.local.User
import com.example.nutrisee.databinding.ActivityLoginBinding
import com.example.nutrisee.utils.Result
import com.example.nutrisee.viewmodel.LoginViewModel

@ExperimentalGetImage class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username: TextView = findViewById(R.id.edt_email)

        viewModel = ViewModelProvider(
            this, ViewModelFactory(application)
        )[LoginViewModel::class.java]

        binding.btnSignIn.setOnClickListener {
            login()
        }
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login() {
        binding.let {
            val email = it.edtEmail.text.toString()
            val password = it.edtPassword.text.toString()
            viewModel.login(email, password).observe(this@LoginActivity) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }

                    is Result.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this@LoginActivity).apply {
                            setTitle("Halo !")
                            setMessage("Selamat datang")
                            setCancelable(false)
                            setPositiveButton("OK") { _, _ ->
                                val idToken = result.data?.id_token as String
                                val displayName = result.data.loginResult?.display_name
                                val photoUrl = result.data.loginResult?.photo_url
                                viewModel.saveUser(User(idToken, email))
                                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is Result.Error -> {
                        showLoading(false)
                        binding.pbLoading.visibility = View.GONE
                        val errorMessage = result.message ?: "Gagal melakukan Login"
                        Log.e("LoginActivity", errorMessage)
                        Toast.makeText(
                            applicationContext,
                            "Gagal melakukan Login",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {}
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.edtEmail.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.edtPassword.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.btnSignIn.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvSignUp.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvDontHaveAcct.visibility = if (isLoading) View.GONE else View.VISIBLE
    }
}