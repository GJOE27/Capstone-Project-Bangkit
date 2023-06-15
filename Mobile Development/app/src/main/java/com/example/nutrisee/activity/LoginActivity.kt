package com.example.nutrisee.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.ExperimentalGetImage
import androidx.core.content.ContextCompat
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

        playPropertyAnimation()
    }

    private fun login() {
        binding.let {
            val email = it.edtEmail.text.toString()
            val password = it.edtPassword.text.toString()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this@LoginActivity, R.string.fill_blanks, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.login(email, password).observe(this@LoginActivity) { result ->
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                            binding.tvHaveU.visibility = View.GONE
                        }

                        is Result.Success -> {
                            binding.tvHaveU.visibility = View.VISIBLE
                            val idToken = result.data?.id_token as String
                            showLoading(false)
                            val builder = AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Hi $email!")
                                setMessage("${getString(R.string.welcome)} NutriSee")
                                setCancelable(false)
                                setPositiveButton("OK") { dialog, which ->
                                    viewModel.saveUser(User(idToken, email))
                                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }

                            val dialog = builder.create()
                            dialog.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)

                            dialog.show()
                        }

                        is Result.Error -> {
                            binding.tvHaveU.visibility = View.VISIBLE
                            showLoading(false)
                            binding.pbLoading.visibility = View.GONE
                            val errorMessage = result.message ?: getString(R.string.error_login)
                            Log.e("LoginActivity", errorMessage)
                            Toast.makeText(
                                applicationContext,
                                R.string.error_login,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun alertDialogBuilder(isBoolean: Boolean) {

    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.edtEmail.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.edtPassword.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.btnSignIn.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvSignUp.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.tvDontHaveAcct.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun playPropertyAnimation() {
        val email = ObjectAnimator.ofFloat(binding.edtEmail, View.ALPHA, 1f).setDuration(500)
        val password = ObjectAnimator.ofFloat(binding.edtPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnSignIn, View.ALPHA, 1f).setDuration(500)
        val tvSignUp = ObjectAnimator.ofFloat(binding.tvDontHaveAcct, View.ALPHA, 1f).setDuration(500)
        val signUp = ObjectAnimator.ofFloat(binding.tvSignUp, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(email, password,btnLogin, tvSignUp, signUp)
            start()
        }
    }
}