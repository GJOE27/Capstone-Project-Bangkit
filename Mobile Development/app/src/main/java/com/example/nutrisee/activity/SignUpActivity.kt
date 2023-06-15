package com.example.nutrisee.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.ExperimentalGetImage
import androidx.lifecycle.ViewModelProvider
import com.example.nutrisee.R
import com.example.nutrisee.ViewModelFactory
import com.example.nutrisee.databinding.ActivitySignUpBinding
import com.example.nutrisee.utils.Result
import com.example.nutrisee.viewmodel.SignUpViewModel

@ExperimentalGetImage
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var viewModel: SignUpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel =
            ViewModelProvider(
                this, ViewModelFactory(application)
            )[SignUpViewModel::class.java]

        binding.btnSignUp.setOnClickListener {
            setupSignUp()
        }
        playPropertyAnimation()
    }

    private fun setupSignUp() {
        binding.apply {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            viewModel.signUpUser(email, password)
                .observe(this@SignUpActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            binding.apply {
                                pbLoading.visibility = View.VISIBLE
                                tvLoading.visibility = View.VISIBLE
                                btnSignUp.visibility = View.GONE
                                edtEmail.visibility = View.GONE
                                edtPassword.visibility = View.GONE
                            }
                        }

                        is Result.Success -> {
                            binding.apply {
                                pbLoading.visibility = View.GONE
                                tvLoading.visibility = View.GONE
                                btnSignUp.visibility = View.VISIBLE
                                edtEmail.visibility = View.VISIBLE
                                edtPassword.visibility = View.VISIBLE
                            }
                            Toast.makeText(
                                this@SignUpActivity, "${getString(R.string.welcome_signup)} $email",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent =
                                Intent(this@SignUpActivity, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }

                        is Result.Error -> {
                            binding.apply {
                                pbLoading.visibility = View.GONE
                                tvLoading.visibility = View.GONE
                                btnSignUp.visibility = View.VISIBLE
                                edtEmail.visibility = View.VISIBLE
                                edtPassword.visibility = View.VISIBLE
                            }
                            val errorMessage = result.message ?: "Gagal melakukan Sign Up"
                            Log.e("SignUpActivity", errorMessage)
                            Toast.makeText(
                                applicationContext,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
        }
    }

    private fun playPropertyAnimation() {

        val title = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnSignUp = ObjectAnimator.ofFloat(binding.btnSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, email, password, btnSignUp)
            start()
        }
    }

}